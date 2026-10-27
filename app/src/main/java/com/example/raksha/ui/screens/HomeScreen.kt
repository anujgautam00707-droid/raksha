package com.example.raksha.ui.screens

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.raksha.sms.SendState
import com.example.raksha.sms.SmsOverallStatus
import com.example.raksha.ui.viewmodel.SosUiState
import com.example.raksha.ui.viewmodel.SosViewModel

@Composable
fun HomeScreen() {
    val context = LocalContext.current
    val application = remember(context) { context.applicationContext as Application }
    val viewModel: SosViewModel = viewModel(
        factory = remember(application) { SosViewModel.Factory(application) }
    )
    val uiState by viewModel.uiState.collectAsState()

    val permissionsToRequest = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val smsGranted = permissions[Manifest.permission.SEND_SMS] == true
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        viewModel.updateSmsPermissionStatus(smsGranted)
        viewModel.updateLocationPermissionStatus(locationGranted)

        if (smsGranted || uiState.isTestMode) {
            viewModel.triggerSos(context)
        } else {
            Toast.makeText(context, "SEND_SMS permission is required to send emergency alerts", Toast.LENGTH_LONG).show()
        }
    }

    // Refresh contact list on display
    LaunchedEffect(Unit) {
        viewModel.loadContacts()
        val hasSms = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
        val hasLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        viewModel.updateSmsPermissionStatus(hasSms)
        viewModel.updateLocationPermissionStatus(hasLoc)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TestModeBanner(
            isTestMode = uiState.isTestMode,
            onToggleTestMode = { enabled -> viewModel.setTestMode(enabled) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        SosButton(
            isSosActive = uiState.isSosActive,
            onClick = {
                val hasSmsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                if (!uiState.isTestMode && !hasSmsPermission) {
                    permissionLauncher.launch(permissionsToRequest)
                } else {
                    viewModel.triggerSos(context)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(visible = uiState.isSosActive) {
            Button(
                onClick = { viewModel.cancelSos() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel SOS Alert")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SecondaryActions(
            onCallEmergency = {
                Toast.makeText(context, "Call Emergency requested", Toast.LENGTH_SHORT).show()
            },
            onCallTrusted = {
                Toast.makeText(context, "Call Trusted Contact requested", Toast.LENGTH_SHORT).show()
            },
            onAlarm = {
                Toast.makeText(context, "Alarm requested", Toast.LENGTH_SHORT).show()
            },
            onFakeCall = {
                Toast.makeText(context, "Fake Call requested", Toast.LENGTH_SHORT).show()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        StatusCardsGrid(uiState = uiState)

        Spacer(modifier = Modifier.height(16.dp))

        if (uiState.contactStatuses.isNotEmpty()) {
            SmsContactStatusSection(uiState = uiState)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun TestModeBanner(
    isTestMode: Boolean,
    onToggleTestMode: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isTestMode) MaterialTheme.colorScheme.tertiaryContainer
                else MaterialTheme.colorScheme.primaryContainer
            )
            .padding(horizontal = 16.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (isTestMode) "⚠ TEST MODE — Mock SMS dispatch" else "🔴 LIVE MODE — Real SMS dispatch",
                color = if (isTestMode) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isTestMode) "Test" else "Live",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(end = 6.dp)
                )
                Switch(
                    checked = !isTestMode,
                    onCheckedChange = { isLive -> onToggleTestMode(!isLive) }
                )
            }
        }
    }
}

@Composable
private fun SosButton(
    isSosActive: Boolean,
    onClick: () -> Unit
) {
    val buttonColor = MaterialTheme.colorScheme.error

    Box(
        modifier = Modifier
            .size(220.dp)
            .shadow(12.dp, CircleShape)
            .clip(CircleShape)
            .background(buttonColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "SOS",
                color = MaterialTheme.colorScheme.onError,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isSosActive) "ALERT ACTIVE" else "Tap for help",
                color = MaterialTheme.colorScheme.onError,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun SecondaryActions(
    onCallEmergency: () -> Unit,
    onCallTrusted: () -> Unit,
    onAlarm: () -> Unit,
    onFakeCall: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton("Call Emergency", Modifier.weight(1f), onCallEmergency)
            ActionButton("Call Trusted", Modifier.weight(1f), onCallTrusted)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ActionButton("Alarm", Modifier.weight(1f), onAlarm)
            ActionButton("Fake Call", Modifier.weight(1f), onFakeCall)
        }
    }
}

@Composable
private fun ActionButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(text = text, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
}

@Composable
private fun StatusCardsGrid(uiState: SosUiState) {
    val sosStatusText = when (val sms = uiState.smsStatus) {
        is SmsOverallStatus.Idle -> if (uiState.isSosActive) "Active" else "Inactive"
        is SmsOverallStatus.Preparing -> "Preparing SMS…"
        is SmsOverallStatus.Sending -> "Sending SMS (${sms.count})…"
        is SmsOverallStatus.Completed -> {
            val sent = sms.statuses.count { it.sendState == SendState.SENT_SUCCESS || it.sendState == SendState.DELIVERED }
            val deliv = sms.statuses.count { it.sendState == SendState.DELIVERED }
            "Sent: $sent | Deliv: $deliv"
        }
        is SmsOverallStatus.PermissionDenied -> "Permission Denied"
        is SmsOverallStatus.NoContacts -> "No Contacts Set"
        is SmsOverallStatus.Error -> "SMS Error"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatusCard("GPS", uiState.locationStatusText, Modifier.weight(1f))
            StatusCard("Signal", "Strong", Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatusCard("Contacts", "${uiState.contacts.size} Configured", Modifier.weight(1f))
            StatusCard("SOS SMS", sosStatusText, Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatusCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun SmsContactStatusSection(uiState: SosUiState) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "SMS Dispatch Status",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            uiState.contactStatuses.forEach { status ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = status.contact.name,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = status.contact.phoneNumber,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val statusText = when (status.sendState) {
                        SendState.PENDING -> "⏳ Pending"
                        SendState.SENT_SUCCESS -> "📤 Sent"
                        SendState.SENT_FAILED -> "❌ Send Failed"
                        SendState.DELIVERED -> "✅ Delivered"
                        SendState.DELIVERY_FAILED -> "⚠️ Delivery Failed"
                        SendState.INVALID_NUMBER -> "🚫 Invalid Number"
                    }
                    val textColor = when (status.sendState) {
                        SendState.DELIVERED -> MaterialTheme.colorScheme.primary
                        SendState.SENT_SUCCESS -> MaterialTheme.colorScheme.secondary
                        SendState.SENT_FAILED, SendState.INVALID_NUMBER, SendState.DELIVERY_FAILED -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(
                        text = statusText,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}
