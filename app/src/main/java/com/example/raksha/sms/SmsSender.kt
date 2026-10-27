package com.example.raksha.sms

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.example.raksha.data.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap

interface SmsSender {
    suspend fun sendEmergencySms(
        contacts: List<Contact>,
        message: String,
        onStatusUpdate: (List<ContactSmsStatus>) -> Unit
    )
}

class AndroidSmsSender(private val context: Context) : SmsSender {

    override suspend fun sendEmergencySms(
        contacts: List<Contact>,
        message: String,
        onStatusUpdate: (List<ContactSmsStatus>) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            val initialStatuses = contacts.map {
                ContactSmsStatus(it, SendState.SENT_FAILED, "SEND_SMS permission not granted")
            }
            onStatusUpdate(initialStatuses)
            return@withContext
        }

        if (contacts.isEmpty()) {
            onStatusUpdate(emptyList())
            return@withContext
        }

        val currentStatuses = ConcurrentHashMap<String, ContactSmsStatus>()
        contacts.forEach { contact ->
            if (PhoneNumberValidator.isValid(contact.phoneNumber)) {
                currentStatuses[contact.id] = ContactSmsStatus(contact, SendState.PENDING)
            } else {
                currentStatuses[contact.id] = ContactSmsStatus(
                    contact = contact,
                    sendState = SendState.INVALID_NUMBER,
                    errorMessage = "Invalid phone number format"
                )
            }
        }
        onStatusUpdate(currentStatuses.values.toList())

        val validContacts = contacts.filter { currentStatuses[it.id]?.sendState == SendState.PENDING }
        if (validContacts.isEmpty()) {
            return@withContext
        }

        val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(SmsManager::class.java)
        } else {
            @Suppress("DEPRECATION")
            SmsManager.getDefault()
        }

        val sentActionPrefix = "com.example.raksha.SMS_SENT_"
        val deliveredActionPrefix = "com.example.raksha.SMS_DELIVERED_"

        validContacts.forEach { contact ->
            val sentAction = "$sentActionPrefix${contact.id}"
            val deliveredAction = "$deliveredActionPrefix${contact.id}"

            val sentReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    val state = if (resultCode == Activity.RESULT_OK) {
                        SendState.SENT_SUCCESS
                    } else {
                        SendState.SENT_FAILED
                    }
                    val err = if (state == SendState.SENT_FAILED) "Sent failed code: $resultCode" else null
                    currentStatuses[contact.id] = ContactSmsStatus(contact, state, err)
                    onStatusUpdate(currentStatuses.values.toList())
                    try {
                        context.unregisterReceiver(this)
                    } catch (_: Exception) {}
                }
            }

            val deliveredReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    val state = if (resultCode == Activity.RESULT_OK) {
                        SendState.DELIVERED
                    } else {
                        SendState.DELIVERY_FAILED
                    }
                    val err = if (state == SendState.DELIVERY_FAILED) "Delivery failed code: $resultCode" else null
                    currentStatuses[contact.id] = ContactSmsStatus(contact, state, err)
                    onStatusUpdate(currentStatuses.values.toList())
                    try {
                        context.unregisterReceiver(this)
                    } catch (_: Exception) {}
                }
            }

            val flag = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(sentReceiver, IntentFilter(sentAction), Context.RECEIVER_EXPORTED)
                context.registerReceiver(deliveredReceiver, IntentFilter(deliveredAction), Context.RECEIVER_EXPORTED)
            } else {
                context.registerReceiver(sentReceiver, IntentFilter(sentAction))
                context.registerReceiver(deliveredReceiver, IntentFilter(deliveredAction))
            }

            val sentIntent = PendingIntent.getBroadcast(context, contact.id.hashCode(), Intent(sentAction), flag)
            val deliveredIntent = PendingIntent.getBroadcast(context, contact.id.hashCode(), Intent(deliveredAction), flag)

            try {
                val parts = smsManager.divideMessage(message)
                if (parts.size > 1) {
                    val sentIntents = ArrayList<PendingIntent>().apply { repeat(parts.size) { add(sentIntent) } }
                    val deliveredIntents = ArrayList<PendingIntent>().apply { repeat(parts.size) { add(deliveredIntent) } }
                    smsManager.sendMultipartTextMessage(
                        contact.phoneNumber, null, parts, sentIntents, deliveredIntents
                    )
                } else {
                    smsManager.sendTextMessage(
                        contact.phoneNumber, null, message, sentIntent, deliveredIntent
                    )
                }
            } catch (e: Exception) {
                currentStatuses[contact.id] = ContactSmsStatus(
                    contact, SendState.SENT_FAILED, e.localizedMessage ?: "Failed to dispatch SMS"
                )
                onStatusUpdate(currentStatuses.values.toList())
            }
        }
    }
}

class MockSmsSender : SmsSender {
    override suspend fun sendEmergencySms(
        contacts: List<Contact>,
        message: String,
        onStatusUpdate: (List<ContactSmsStatus>) -> Unit
    ) = withContext(Dispatchers.IO) {
        if (contacts.isEmpty()) {
            onStatusUpdate(emptyList())
            return@withContext
        }

        val statuses = contacts.map { contact ->
            if (PhoneNumberValidator.isValid(contact.phoneNumber)) {
                ContactSmsStatus(contact, SendState.PENDING)
            } else {
                ContactSmsStatus(contact, SendState.INVALID_NUMBER, "Invalid phone number format")
            }
        }.toMutableList()

        onStatusUpdate(statuses.toList())
        delay(300)

        for (i in statuses.indices) {
            if (statuses[i].sendState == SendState.PENDING) {
                statuses[i] = statuses[i].copy(sendState = SendState.SENT_SUCCESS)
            }
        }
        onStatusUpdate(statuses.toList())
        delay(300)

        for (i in statuses.indices) {
            if (statuses[i].sendState == SendState.SENT_SUCCESS) {
                statuses[i] = statuses[i].copy(sendState = SendState.DELIVERED)
            }
        }
        onStatusUpdate(statuses.toList())
    }
}
