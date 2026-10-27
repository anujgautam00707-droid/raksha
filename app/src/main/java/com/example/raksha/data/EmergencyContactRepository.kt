package com.example.raksha.data

import android.content.Context
import android.content.SharedPreferences
import com.example.raksha.sms.PhoneNumberValidator
import org.json.JSONArray
import org.json.JSONObject

class EmergencyContactRepository(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("raksha_emergency_contacts", Context.MODE_PRIVATE)

    fun getContacts(): List<Contact> {
        val jsonString = prefs.getString(KEY_CONTACTS, null) ?: return emptyList()
        val list = mutableListOf<Contact>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Contact(
                        id = obj.optString("id", i.toString()),
                        name = obj.optString("name", "Contact"),
                        phoneNumber = obj.optString("phoneNumber", ""),
                        isEmergencyContact = obj.optBoolean("isEmergencyContact", true)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun getValidatedEmergencyContacts(): List<Contact> {
        return getContacts().filter { contact ->
            contact.isEmergencyContact && PhoneNumberValidator.isValid(contact.phoneNumber)
        }
    }

    fun saveContacts(contacts: List<Contact>) {
        val jsonArray = JSONArray()
        for (contact in contacts) {
            val obj = JSONObject().apply {
                put("id", contact.id)
                put("name", contact.name)
                put("phoneNumber", contact.phoneNumber)
                put("isEmergencyContact", contact.isEmergencyContact)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_CONTACTS, jsonArray.toString()).apply()
    }

    fun addContact(name: String, phoneNumber: String): Boolean {
        if (name.isBlank() || !PhoneNumberValidator.isValid(phoneNumber)) return false
        val current = getContacts().toMutableList()
        val newContact = Contact(
            id = System.currentTimeMillis().toString(),
            name = name.trim(),
            phoneNumber = PhoneNumberValidator.normalize(phoneNumber),
            isEmergencyContact = true
        )
        current.add(newContact)
        saveContacts(current)
        return true
    }

    fun deleteContact(id: String) {
        val current = getContacts().filterNot { it.id == id }
        saveContacts(current)
    }

    companion object {
        private const val KEY_CONTACTS = "key_contacts_list"
    }
}
