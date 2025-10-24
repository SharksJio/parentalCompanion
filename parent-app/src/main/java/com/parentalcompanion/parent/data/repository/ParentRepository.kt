package com.parentalcompanion.parent.data.repository

import com.google.firebase.database.*
import com.parentalcompanion.parent.data.model.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ParentRepository {
    
    private val database = FirebaseDatabase.getInstance()
    private val devicesRef = database.getReference("devices")
    private val screenTimeRef = database.getReference("screenTime")
    private val appControlRef = database.getReference("appControl")
    private val contactsRef = database.getReference("contacts")
    private val locationsRef = database.getReference("locations")
    private val geofencesRef = database.getReference("geofences")
    
    // Child Device Management
    fun observeChildDevice(deviceId: String): Flow<ChildDevice?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val device = snapshot.getValue(ChildDevice::class.java)
                trySend(device)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        devicesRef.child(deviceId).addValueEventListener(listener)
        awaitClose { devicesRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun updateDeviceLockStatus(deviceId: String, isLocked: Boolean) {
        devicesRef.child(deviceId).child("isLocked").setValue(isLocked).await()
    }
    
    // Screen Time Management
    fun observeScreenTime(deviceId: String): Flow<ScreenTimeLimit?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val screenTime = snapshot.getValue(ScreenTimeLimit::class.java)
                trySend(screenTime)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        screenTimeRef.child(deviceId).addValueEventListener(listener)
        awaitClose { screenTimeRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun setScreenTimeLimit(deviceId: String, dailyLimitMinutes: Int) {
        val limit = ScreenTimeLimit(deviceId, dailyLimitMinutes, 0)
        screenTimeRef.child(deviceId).setValue(limit).await()
    }
    
    // App Control Management
    fun observeAppControls(deviceId: String): Flow<List<AppControl>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val apps = snapshot.children.mapNotNull { 
                    it.getValue(AppControl::class.java)
                }
                trySend(apps)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        appControlRef.child(deviceId).addValueEventListener(listener)
        awaitClose { appControlRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun updateAppControl(deviceId: String, appControl: AppControl) {
        appControlRef.child(deviceId).child(appControl.packageName).setValue(appControl).await()
    }
    
    // Contact Management
    fun observeContacts(deviceId: String): Flow<List<ContactControl>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val contacts = snapshot.children.mapNotNull {
                    it.getValue(ContactControl::class.java)
                }
                trySend(contacts)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        contactsRef.child(deviceId).addValueEventListener(listener)
        awaitClose { contactsRef.child(deviceId).removeEventListener(listener) }
    }
    
    suspend fun updateContact(deviceId: String, contact: ContactControl) {
        contactsRef.child(deviceId).child(contact.contactId).setValue(contact).await()
    }
    
    // Location Management
    fun observeLocation(deviceId: String): Flow<LocationData?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val location = snapshot.getValue(LocationData::class.java)
                trySend(location)
            }
            
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        
        locationsRef.child(deviceId).child("current").addValueEventListener(listener)
        awaitClose { locationsRef.child(deviceId).child("current").removeEventListener(listener) }
    }
    
    suspend fun requestLocationUpdate(deviceId: String) {
        devicesRef.child(deviceId).child("requestLocation").setValue(true).await()
    }
    
    // Geofence Management
    fun observeGeofences(deviceId: String): Flow<List<Geofence>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val geofences = snapshot.children.mapNotNull {
                    it.getValue(Geofence::class.java)
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
    
    suspend fun addGeofence(deviceId: String, geofence: Geofence) {
        geofencesRef.child(deviceId).child(geofence.id).setValue(geofence).await()
    }
    
    suspend fun deleteGeofence(deviceId: String, geofenceId: String) {
        geofencesRef.child(deviceId).child(geofenceId).removeValue().await()
    }
}
