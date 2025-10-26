package com.parentalcompanion.child.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.parentalcompanion.child.R
import com.parentalcompanion.child.data.repository.ChildRepository
import kotlinx.coroutines.launch

class LocationService : LifecycleService() {
    
    private val repository = ChildRepository()
    private var deviceId: String = "test_device_id" // TODO: Get from SharedPreferences
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var geofenceMonitor: GeofenceMonitor
    
    companion object {
        private const val CHANNEL_ID = "location_service_channel"
        private const val NOTIFICATION_ID = 2
        private const val LOCATION_UPDATE_INTERVAL = 300000L // 5 minutes
        private const val FASTEST_LOCATION_UPDATE_INTERVAL = 60000L // 1 minute
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofenceMonitor = GeofenceMonitor(this, fusedLocationClient)
        
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
        
        startLocationUpdates()
        
        // Observe location request
        lifecycleScope.launch {
            repository.observeLocationRequest(deviceId).collect { requested ->
                if (requested) {
                    getCurrentLocation()
                }
            }
        }
        
        // Observe geofences and monitor them
        lifecycleScope.launch {
            repository.observeGeofences(deviceId).collect { geofences ->
                geofenceMonitor.checkGeofences(geofences)
            }
        }
    }
    
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            LOCATION_UPDATE_INTERVAL
        ).apply {
            setMinUpdateIntervalMillis(FASTEST_LOCATION_UPDATE_INTERVAL)
        }.build()
        
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let { updateLocation(it) }
        }
    }
    
    private fun updateLocation(location: Location) {
        lifecycleScope.launch {
            repository.updateLocation(
                deviceId,
                location.latitude,
                location.longitude,
                location.accuracy
            )
        }
        
        // Also check geofences when location updates
        lifecycleScope.launch {
            repository.observeGeofences(deviceId).collect { geofences ->
                geofenceMonitor.checkGeofences(geofences)
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Location tracking service notification"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.location_notification_title))
            .setContentText(getString(R.string.location_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
