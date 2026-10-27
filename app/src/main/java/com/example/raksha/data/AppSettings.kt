package com.example.raksha.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppSettings {
    private lateinit var prefs: SharedPreferences

    var userName by mutableStateOf("")
        private set
    var emergencyNumber by mutableStateOf("112")
        private set
    var shareGps by mutableStateOf(true)
        private set
    var sirenEnabled by mutableStateOf(true)
        private set
    // null = follow the phone's setting
    var darkMode by mutableStateOf<Boolean?>(null)
        private set

    fun init(context: Context) {
        prefs = context.applicationContext
            .getSharedPreferences("raksha_settings", Context.MODE_PRIVATE)
        userName = prefs.getString(KEY_NAME, "") ?: ""
        emergencyNumber = prefs.getString(KEY_NUMBER, "112") ?: "112"
        shareGps = prefs.getBoolean(KEY_GPS, true)
        sirenEnabled = prefs.getBoolean(KEY_SIREN, true)
        darkMode = if (prefs.contains(KEY_DARK)) prefs.getBoolean(KEY_DARK, false) else null
    }

    fun updateUserName(value: String) {
        userName = value
        prefs.edit().putString(KEY_NAME, value).apply()
    }

    fun updateEmergencyNumber(value: String) {
        emergencyNumber = value
        prefs.edit().putString(KEY_NUMBER, value).apply()
    }

    fun updateShareGps(value: Boolean) {
        shareGps = value
        prefs.edit().putBoolean(KEY_GPS, value).apply()
    }

    fun updateSirenEnabled(value: Boolean) {
        sirenEnabled = value
        prefs.edit().putBoolean(KEY_SIREN, value).apply()
    }

    fun updateDarkMode(value: Boolean) {
        darkMode = value
        prefs.edit().putBoolean(KEY_DARK, value).apply()
    }

    private const val KEY_NAME = "user_name"
    private const val KEY_NUMBER = "emergency_number"
    private const val KEY_GPS = "share_gps"
    private const val KEY_SIREN = "siren_enabled"
    private const val KEY_DARK = "dark_mode"
}