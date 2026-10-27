package com.example.raksha.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.raksha.data.Contact
import com.example.raksha.data.EmergencyContactRepository
import com.example.raksha.data.EmergencyLocation
import com.example.raksha.data.LocationProvider
import com.example.raksha.sms.AndroidSmsSender
import com.example.raksha.sms.ContactSmsStatus
import com.example.raksha.sms.MockSmsSender
import com.example.raksha.sms.SmsMessageFormatter
import com.example.raksha.sms.SmsOverallStatus
import com.example.raksha.sms.SmsSender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class SosUiState(
    val isSosActive: Boolean = false,
    val isTestMode: Boolean = true, // Default to test mode to avoid silent real SMS during development/testing
    val location: EmergencyLocation? = null,
    val locationStatusText: String = "Locating…",
    val smsStatus: SmsOverallStatus = SmsOverallStatus.Idle,
    val contactStatuses: List<ContactSmsStatus> = emptyList(),
    val contacts: List<Contact> = emptyList(),
    val isPermissionGranted: Boolean = false,
    val hasLocationPermission: Boolean = false,
    val lastTriggerTimestamp: Long = 0L
)

class SosViewModel @JvmOverloads constructor(
    application: Application,
    private val contactRepo: EmergencyContactRepository = EmergencyContactRepository(application),
    private val locationProvider: LocationProvider = LocationProvider(application),
    private val realSmsSender: SmsSender = AndroidSmsSender(application),
    private val mockSmsSender: SmsSender = MockSmsSender()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(SosUiState())
    val uiState: StateFlow<SosUiState> = _uiState.asStateFlow()

    private val sosTriggerMutex = Mutex()
    private var isDispatchingSms = false

    init {
        loadContacts()
    }

    fun loadContacts() {
        viewModelScope.launch(Dispatchers.IO) {
            val contactsList = contactRepo.getContacts()
            _uiState.update { it.copy(contacts = contactsList) }
        }
    }

    fun setTestMode(enabled: Boolean) {
        _uiState.update { it.copy(isTestMode = enabled) }
    }

    fun updateSmsPermissionStatus(isGranted: Boolean) {
        _uiState.update { it.copy(isPermissionGranted = isGranted) }
    }

    fun updateLocationPermissionStatus(isGranted: Boolean) {
        _uiState.update { it.copy(hasLocationPermission = isGranted) }
    }

    fun triggerSos(context: Context? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            sosTriggerMutex.withLock {
                val now = System.currentTimeMillis()
                val state = _uiState.value

                // Prevent duplicate sends from repeated button taps within 3 seconds
                if (isDispatchingSms || (now - state.lastTriggerTimestamp < 3000L)) {
                    return@launch
                }

                isDispatchingSms = true
                _uiState.update {
                    it.copy(
                        isSosActive = true,
                        lastTriggerTimestamp = now,
                        smsStatus = SmsOverallStatus.Preparing
                    )
                }

                // Location fetch runs independently
                val emergencyLocation = try {
                    locationProvider.getCurrentOrLastLocation()
                } catch (_: Exception) {
                    null
                }

                _uiState.update {
                    it.copy(
                        location = emergencyLocation,
                        locationStatusText = emergencyLocation?.toCoordinatesString() ?: "Location unavailable"
                    )
                }

                val allContacts = contactRepo.getContacts()
                if (allContacts.isEmpty()) {
                    _uiState.update {
                        it.copy(smsStatus = SmsOverallStatus.NoContacts)
                    }
                    isDispatchingSms = false
                    return@launch
                }

                val isTest = _uiState.value.isTestMode
                if (!isTest && !_uiState.value.isPermissionGranted) {
                    _uiState.update {
                        it.copy(smsStatus = SmsOverallStatus.PermissionDenied)
                    }
                    isDispatchingSms = false
                    return@launch
                }

                val message = SmsMessageFormatter.formatEmergencyMessage(emergencyLocation, now)
                val senderToUse = if (isTest) mockSmsSender else realSmsSender

                _uiState.update {
                    it.copy(smsStatus = SmsOverallStatus.Sending(allContacts.size))
                }

                senderToUse.sendEmergencySms(
                    contacts = allContacts,
                    message = message,
                    onStatusUpdate = { updatedStatuses ->
                        _uiState.update { currentState ->
                            currentState.copy(
                                contactStatuses = updatedStatuses,
                                smsStatus = SmsOverallStatus.Completed(updatedStatuses)
                            )
                        }
                    }
                )

                isDispatchingSms = false
            }
        }
    }

    fun cancelSos() {
        _uiState.update {
            it.copy(
                isSosActive = false,
                smsStatus = SmsOverallStatus.Idle,
                contactStatuses = emptyList()
            )
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SosViewModel::class.java)) {
                return SosViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
