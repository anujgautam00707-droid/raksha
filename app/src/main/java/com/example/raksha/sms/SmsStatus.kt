package com.example.raksha.sms

import com.example.raksha.data.Contact

enum class SendState {
    PENDING,
    SENT_SUCCESS,
    SENT_FAILED,
    DELIVERED,
    DELIVERY_FAILED,
    INVALID_NUMBER
}

data class ContactSmsStatus(
    val contact: Contact,
    val sendState: SendState = SendState.PENDING,
    val errorMessage: String? = null
)

sealed class SmsOverallStatus {
    data object Idle : SmsOverallStatus()
    data object Preparing : SmsOverallStatus()
    data class Sending(val count: Int) : SmsOverallStatus()
    data class Completed(val statuses: List<ContactSmsStatus>) : SmsOverallStatus()
    data object PermissionDenied : SmsOverallStatus()
    data object NoContacts : SmsOverallStatus()
    data class Error(val message: String) : SmsOverallStatus()
}
