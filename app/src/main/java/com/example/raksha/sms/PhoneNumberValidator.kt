package com.example.raksha.sms

object PhoneNumberValidator {
    /**
     * Normalizes a phone number by removing formatting characters (spaces, dashes, parens).
     */
    fun normalize(phoneNumber: String): String {
        val trimmed = phoneNumber.trim()
        val hasLeadingPlus = trimmed.startsWith("+")
        val digitsOnly = trimmed.replace(Regex("[^0-9]"), "")
        return if (hasLeadingPlus) "+$digitsOnly" else digitsOnly
    }

    /**
     * Validates if a phone number is suitable for emergency SMS dispatch.
     * Requires at least 7 digits and at most 15 digits (E.164 compliant length),
     * and must contain only numbers (with optional leading '+').
     */
    fun isValid(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrBlank()) return false
        val normalized = normalize(phoneNumber)
        val phoneRegex = Regex("^[+]?[0-9]{7,15}$")
        return phoneRegex.matches(normalized)
    }
}
