package com.parentalcompanion.child.data.repository

import com.google.firebase.database.*
import com.parentalcompanion.child.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChildRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val devicesRef = database.getReference("devices")
    private val screenTimeRef = database.getReference("screenTime")
    private val appControlRef = database.getReference("appControl")
    private val contactsRef = database.getReference("contacts")
    private val locationsRef = database.getReference("locations")
    private val geofencesRef = database.getReference("geofences")
    
    init {
        // Ensure Firebase connection is active
        database.goOnline()
    }
    
    suspend fun updateDeviceStatus(deviceId: String, isOnline: Boolean) {
        devicesRef.child(deviceId).child("isOnline").setValue(isOnline).await()
        devicesRef.child(deviceId).child("lastSeen").setValue(System.currentTimeMillis()).await()
    }
    
    fun observeLockStatus(deviceId: String): Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val isLocked = snapshot.getValue(Boolean::class.java) ?: false
                trySend(isLocked)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        devicesRef.child(deviceId).child("isLocked").addValueEventListener(listener)
        awaitClose { devicesRef.child(deviceId).child("isLocked").removeEventListener(listener) }
    }
    
    fun observeScreenTimeLimit(deviceId: String): Flow<Int> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val limit = snapshot.child("dailyLimitMinutes").getValue(Int::class.java) ?: 0
                trySend(limit)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        screenTimeRef.child(deviceId).addValueEventListener(listener)
        awaitClose { screenTimeRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun updateScreenTimeUsage(deviceId: String, usedMinutes: Int) {
        screenTimeRef.child(deviceId).child("usedMinutesToday").setValue(usedMinutes).await()
        screenTimeRef.child(deviceId).child("lastUpdated").setValue(System.currentTimeMillis()).await()
    }
    
    fun observeAppControls(deviceId: String): Flow<Map<String, Boolean>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val blockedApps = mutableMapOf<String, Boolean>()
                snapshot.children.forEach { child ->
                    val packageName = child.key ?: return@forEach
                    val isBlocked = child.child("isBlocked").getValue(Boolean::class.java) ?: false
                    blockedApps[packageName] = isBlocked
                }
                trySend(blockedApps)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        appControlRef.child(deviceId).addValueEventListener(listener)
        awaitClose { appControlRef.child(deviceId).removeEventListener(listener) }
    }
    
    fun observeAllowedContacts(deviceId: String): Flow<Set<String>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allowed = mutableSetOf<String>()
                snapshot.children.forEach { child ->
                    val isAllowed = child.child("isAllowed").getValue(Boolean::class.java) ?: true
                    if (isAllowed) {
                        val phoneNumber = child.child("phoneNumber").getValue(String::class.java)
                        phoneNumber?.let { allowed.add(it) }
                    }
                }
                trySend(allowed)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        contactsRef.child(deviceId).addValueEventListener(listener)
        awaitClose { contactsRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun updateLocation(deviceId: String, latitude: Double, longitude: Double, accuracy: Float) {
        val locationData = mapOf(
            "deviceId" to deviceId,
            "latitude" to latitude,
            "longitude" to longitude,
            "accuracy" to accuracy,
            "timestamp" to System.currentTimeMillis()
        )
        locationsRef.child(deviceId).child("current").setValue(locationData).await()
    }
    
    fun observeLocationRequest(deviceId: String): Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val requested = snapshot.getValue(Boolean::class.java) ?: false
                trySend(requested)
                if (requested) {
                    devicesRef.child(deviceId).child("requestLocation").setValue(false)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        devicesRef.child(deviceId).child("requestLocation").addValueEventListener(listener)
        awaitClose { devicesRef.child(deviceId).child("requestLocation").removeEventListener(listener) }
    }
    
    fun observeGeofences(deviceId: String): Flow<List<GeofenceData>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val geofences = snapshot.children.mapNotNull {
                    it.getValue(GeofenceData::class.java)
                }
                trySend(geofences)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        geofencesRef.child(deviceId).addValueEventListener(listener)
        awaitClose { geofencesRef.child(deviceId).removeEventListener(listener) }
    }
}
