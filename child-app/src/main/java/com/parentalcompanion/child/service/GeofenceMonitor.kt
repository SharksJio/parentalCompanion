package com.parentalcompanion.child.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.parentalcompanion.child.R
import com.parentalcompanion.child.data.model.GeofenceData

class GeofenceMonitor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {
    
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val geofenceStates = mutableMapOf<String, Boolean>() // Track inside/outside state
    
    companion object {
        private const val TAG = "GeofenceMonitor"
        private const val GEOFENCE_CHANNEL_ID = "geofence_alerts"
        private const val GEOFENCE_NOTIFICATION_BASE_ID = 1000
    }
    
    init {
        createNotificationChannel()
    }
    
    fun checkGeofences(geofences: List<GeofenceData>) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                processGeofences(geofences, it)
            }
        }
    }
    
    private fun processGeofences(geofences: List<GeofenceData>, currentLocation: Location) {
        for (geofence in geofences) {
            if (!geofence.isActive) continue
            
            val distance = calculateDistance(
                currentLocation.latitude,
                currentLocation.longitude,
                geofence.latitude,
                geofence.longitude
            )
            
            val isInside = distance <= geofence.radiusMeters
            val wasInside = geofenceStates[geofence.id] ?: false
            
            // Check for state transitions
            if (isInside && !wasInside && geofence.notifyOnEnter) {
                Log.d(TAG, "Entered geofence: ${geofence.name}")
                sendGeofenceNotification(geofence, true)
            } else if (!isInside && wasInside && geofence.notifyOnExit) {
                Log.d(TAG, "Exited geofence: ${geofence.name}")
                sendGeofenceNotification(geofence, false)
            }
            
            // Update state
            geofenceStates[geofence.id] = isInside
        }
    }
    
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }
    
    private fun sendGeofenceNotification(geofence: GeofenceData, entered: Boolean) {
        val title = if (entered) {
            "Entered ${geofence.name}"
        } else {
            "Left ${geofence.name}"
        }
        
        val message = if (entered) {
            "Child device has entered the geofence area"
        } else {
            "Child device has left the geofence area"
        }
        
        val notificationId = GEOFENCE_NOTIFICATION_BASE_ID + geofence.id.hashCode()
        
        val notification = NotificationCompat.Builder(context, GEOFENCE_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        
        notificationManager.notify(notificationId, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                GEOFENCE_CHANNEL_ID,
                "Geofence Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for geofence entry and exit"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}
