package com.parentalcompanion.child.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.parentalcompanion.child.databinding.ActivityLockScreenBinding

class LockScreenActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLockScreenBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
            WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
        
        val message = intent.getStringExtra("message") ?: "Device is locked"
        binding.tvLockMessage.text = message
        
        val timeInfo = intent.getStringExtra("timeInfo")
        if (timeInfo != null) {
            binding.tvTimeInfo.text = timeInfo
            binding.tvTimeInfo.visibility = android.view.View.VISIBLE
        }
    }
    
    override fun onBackPressed() {
        // Prevent back button from closing lock screen
    }
}
