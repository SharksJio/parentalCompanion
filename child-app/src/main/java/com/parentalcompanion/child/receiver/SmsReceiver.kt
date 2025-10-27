package com.parentalcompanion.child.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import com.parentalcompanion.child.data.repository.ChildRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    
    private val repository = ChildRepository()
    private var deviceId: String = "test_device_id" // TODO: Get from SharedPreferences
    
    companion object {
        private const val TAG = "SmsReceiver"
        private const val SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == SMS_RECEIVED) {
            val bundle = intent.extras
            if (bundle != null) {
                try {
                    val pdus = bundle.get("pdus") as Array<*>
                    val messages = arrayOfNulls<SmsMessage>(pdus.size)
                    
                    for (i in pdus.indices) {
                        messages[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                    }
                    
                    if (messages.isNotEmpty()) {
                        val sender = messages[0]?.originatingAddress
                        
                        sender?.let { number ->
                            // Check if the number is in allowed contacts
                            CoroutineScope(Dispatchers.IO).launch {
                                repository.observeAllowedContacts(deviceId).collect { contacts ->
                                    if (!isNumberAllowed(number, contacts)) {
                                        Log.d(TAG, "Blocking SMS from: $number")
                                        abortBroadcast()
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing SMS", e)
                }
            }
        }
    }
    
    private fun isNumberAllowed(number: String, allowedContacts: Set<String>): Boolean {
        // Normalize the number for comparison
        val normalizedNumber = number.replace(Regex("[^0-9]"), "")
        
        return allowedContacts.any { allowedNumber ->
            val normalizedAllowed = allowedNumber.replace(Regex("[^0-9]"), "")
            normalizedNumber.endsWith(normalizedAllowed) || normalizedAllowed.endsWith(normalizedNumber)
        }
    }
}
