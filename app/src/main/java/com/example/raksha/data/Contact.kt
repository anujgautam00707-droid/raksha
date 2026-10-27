package com.example.raksha.data

data class Contact(
    val id: String,
    val name: String,
    val phoneNumber: String,
    val isEmergencyContact: Boolean = true
)
