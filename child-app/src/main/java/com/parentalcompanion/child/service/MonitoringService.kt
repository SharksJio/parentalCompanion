package com.parentalcompanion.child.service

import android.app.*
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.parentalcompanion.child.R
import com.parentalcompanion.child.data.repository.ChildRepository
import com.parentalcompanion.child.ui.LockScreenActivity
import kotlinx.coroutines.launch

class MonitoringService : LifecycleService() {
    
    private val repository = ChildRepository()
    private var deviceId: String = "test_device_id" // TODO: Get from SharedPreferences
    private val handler = Handler(Looper.getMainLooper())
    private var blockedApps = mutableMapOf<String, Boolean>()
    private var screenTimeLimitMinutes: Int = 0
    private var screenTimeUsedMinutes: Int = 0
    
    companion object {
        private const val CHANNEL_ID = "monitoring_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val APP_CHECK_INTERVAL = 2000L // Check every 2 seconds
        private const val SCREEN_TIME_UPDATE_INTERVAL = 60000L // Update every minute
        private const val TAG = "MonitoringService"
    }
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        // Update device status
        lifecycleScope.launch {
            repository.updateDeviceStatus(deviceId, true)
        }
        
        // Observe lock status
        lifecycleScope.launch {
            repository.observeLockStatus(deviceId).collect { isLocked ->
                if (isLocked) {
                    showLockScreen()
                }
            }
        }
        
        // Observe screen time limits
        lifecycleScope.launch {
            repository.observeScreenTime(deviceId).collect { screenTime ->
                screenTime?.let {
                    screenTimeLimitMinutes = it.dailyLimitMinutes
                    screenTimeUsedMinutes = it.usedMinutesToday
                    checkScreenTimeLimit()
                }
            }
        }
        
        // Observe app controls (blocked apps)
        lifecycleScope.launch {
            repository.observeAppControls(deviceId).collect { appControls ->
                blockedApps = appControls.toMutableMap()
            }
        }
        
        // Observe geofences
        lifecycleScope.launch {
            repository.observeGeofences(deviceId).collect { geofences ->
                // Geofences are monitored - implementation can be extended
                Log.d(TAG, "Geofences updated: ${geofences.size} geofences")
            }
        }
        
        // Start periodic checks
        startAppMonitoring()
        startScreenTimeTracking()
        
        // Start location service
        val locationIntent = Intent(this, LocationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(locationIntent)
        } else {
            startService(locationIntent)
        }
    }
    
    private fun showLockScreen() {
        val intent = Intent(this, LockScreenActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        startActivity(intent)
    }
    
    private fun startAppMonitoring() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                checkRunningApps()
                handler.postDelayed(this, APP_CHECK_INTERVAL)
            }
        }, APP_CHECK_INTERVAL)
    }
    
    private fun checkRunningApps() {
        try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            if (usageStatsManager == null) {
                Log.e(TAG, "UsageStatsManager not available")
                return
            }
            
            val currentTime = System.currentTimeMillis()
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST,
                currentTime - 5000, // Last 5 seconds
                currentTime
            )
            
            if (stats.isNullOrEmpty()) {
                return
            }
            
            // Get the most recently used app
            val recentApp = stats.maxByOrNull { it.lastTimeUsed }
            recentApp?.let { app ->
                if (blockedApps[app.packageName] == true) {
                    // App is blocked, show lock screen or go to home
                    Log.d(TAG, "Blocked app detected: ${app.packageName}")
                    goToHomeScreen()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking running apps", e)
        }
    }
    
    private fun goToHomeScreen() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }
    
    private fun startScreenTimeTracking() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateScreenTimeUsage()
                handler.postDelayed(this, SCREEN_TIME_UPDATE_INTERVAL)
            }
        }, SCREEN_TIME_UPDATE_INTERVAL)
    }
    
    private fun updateScreenTimeUsage() {
        try {
            val usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            if (usageStatsManager == null) {
                return
            }
            
            val currentTime = System.currentTimeMillis()
            val startOfDay = getStartOfDay()
            
            val stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                startOfDay,
                currentTime
            )
            
            if (!stats.isNullOrEmpty()) {
                val totalTimeInForeground = stats.sumOf { it.totalTimeInForeground }
                val totalMinutes = (totalTimeInForeground / 60000).toInt()
                
                screenTimeUsedMinutes = totalMinutes
                
                // Update Firebase
                lifecycleScope.launch {
                    repository.updateScreenTimeUsage(deviceId, totalMinutes)
                }
                
                checkScreenTimeLimit()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating screen time", e)
        }
    }
    
    private fun getStartOfDay(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    private fun checkScreenTimeLimit() {
        if (screenTimeLimitMinutes > 0 && screenTimeUsedMinutes >= screenTimeLimitMinutes) {
            Log.d(TAG, "Screen time limit exceeded: $screenTimeUsedMinutes >= $screenTimeLimitMinutes")
            // Lock device when screen time limit is exceeded
            lifecycleScope.launch {
                // This will trigger the lock status observer above
                // Note: In a real implementation, you might want to update a separate "screen time lock" field
                showLockScreen()
            }
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Child monitoring service notification"
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, com.parentalcompanion.child.ui.MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.monitoring_notification_title))
            .setContentText(getString(R.string.monitoring_notification_text))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        lifecycleScope.launch {
            repository.updateDeviceStatus(deviceId, false)
        }
    }
    
    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
