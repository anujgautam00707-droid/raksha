package com.example.raksha

import com.example.raksha.data.Contact
import com.example.raksha.data.EmergencyLocation
import com.example.raksha.sms.MockSmsSender
import com.example.raksha.sms.PhoneNumberValidator
import com.example.raksha.sms.SendState
import com.example.raksha.sms.SmsMessageFormatter
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsSystemUnitTest {

    @Test
    fun testPhoneNumberValidation() {
        assertTrue(PhoneNumberValidator.isValid("+15551234567"))
        assertTrue(PhoneNumberValidator.isValid("9876543210"))
        assertTrue(PhoneNumberValidator.isValid("+91 98765-43210"))

        assertFalse(PhoneNumberValidator.isValid("12345"))
        assertFalse(PhoneNumberValidator.isValid("abc-def-ghij"))
        assertFalse(PhoneNumberValidator.isValid(""))
        assertFalse(PhoneNumberValidator.isValid(null))
    }

    @Test
    fun testSmsMessageFormatterWithLocation() {
        val location = EmergencyLocation(12.9716, 77.5946)
        val timestamp = 1761540000000L // fixed timestamp

        val message = SmsMessageFormatter.formatEmergencyMessage(location, timestamp)

        assertTrue(message.contains("EMERGENCY ALERT!"))
        assertTrue(message.contains("https://maps.google.com/?q=12.9716,77.5946"))
        assertTrue(message.contains("Lat: 12.9716, Lng: 77.5946"))
        assertFalse(message.contains("Location unavailable"))
    }

    @Test
    fun testSmsMessageFormatterWithoutLocationFallback() {
        val timestamp = 1761540000000L
        val message = SmsMessageFormatter.formatEmergencyMessage(null, timestamp)

        assertTrue(message.contains("EMERGENCY ALERT!"))
        assertTrue(message.contains("Location: Location unavailable."))
        assertFalse(message.contains("maps.google.com"))
    }

    @Test
    fun testMockSmsSenderNonBlockingExecution() = runBlocking {
        val contacts = listOf(
            Contact("1", "Alice", "+15551234567"),
            Contact("2", "Bob", "invalid")
        )
        val sender = MockSmsSender()
        var finalStatuses = emptyList<com.example.raksha.sms.ContactSmsStatus>()

        sender.sendEmergencySms(contacts, "Test Message") { updated ->
            finalStatuses = updated
        }

        assertEquals(2, finalStatuses.size)

        val aliceStatus = finalStatuses.first { it.contact.id == "1" }
        assertEquals(SendState.DELIVERED, aliceStatus.sendState)

        val bobStatus = finalStatuses.first { it.contact.id == "2" }
        assertEquals(SendState.INVALID_NUMBER, bobStatus.sendState)
    }
}
