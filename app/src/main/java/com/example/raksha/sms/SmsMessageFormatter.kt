package com.example.raksha.sms

import com.example.raksha.data.EmergencyLocation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SmsMessageFormatter {

    fun formatEmergencyMessage(
        location: EmergencyLocation?,
        timestampMillis: Long = System.currentTimeMillis()
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val timeStr = dateFormat.format(Date(timestampMillis))

        return if (location != null) {
            "EMERGENCY ALERT! I need urgent help. Time: $timeStr. Location: ${location.toMapsUrl()} (${location.toCoordinatesString()})"
        } else {
            "EMERGENCY ALERT! I need urgent help. Time: $timeStr. Location: Location unavailable."
        }
    }
}
