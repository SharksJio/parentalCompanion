package com.parentalcompanion.child.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.parentalcompanion.child.data.repository.ChildRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.Method

class CallReceiver : BroadcastReceiver() {
    
    private val repository = ChildRepository()
    private var deviceId: String = "test_device_id" // TODO: Get from SharedPreferences
    private var allowedContacts = setOf<String>()
    
    companion object {
        private const val TAG = "CallReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
            
            if (state == TelephonyManager.EXTRA_STATE_RINGING) {
                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
                
                incomingNumber?.let { number ->
                    // Check if the number is in allowed contacts
                    CoroutineScope(Dispatchers.IO).launch {
                        repository.observeAllowedContacts(deviceId).collect { contacts ->
                            allowedContacts = contacts
                            
                            if (!isNumberAllowed(number, contacts)) {
                                Log.d(TAG, "Blocking call from: $number")
                                blockCall()
                            }
                        }
                    }
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
    
    private fun blockCall() {
        try {
            val telephonyService = Class.forName("com.android.internal.telephony.ITelephony")
            val telephonyManager = Class.forName("android.os.ServiceManager")
            val getServiceMethod = telephonyManager.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, Context.TELEPHONY_SERVICE)
            val endCallMethod = telephonyService.getMethod("endCall")
            endCallMethod.invoke(binder)
            Log.d(TAG, "Call blocked successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to block call", e)
        }
    }
}
