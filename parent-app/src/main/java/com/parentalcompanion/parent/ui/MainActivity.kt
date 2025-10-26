package com.parentalcompanion.parent.ui

import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.parentalcompanion.parent.R
import com.parentalcompanion.parent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setSupportActionBar(binding.toolbar)
        
        // Apply window insets to prevent toolbar from overlapping status bar
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.setPadding(
                view.paddingLeft,
                statusBarInsets.top,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }
        ViewCompat.requestApplyInsets(binding.toolbar)
        
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        
        NavigationUI.setupWithNavController(navView, navController)
        
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, binding.toolbar,
            R.string.app_name, R.string.app_name
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> navController.navigate(R.id.dashboardFragment)
                R.id.nav_screen_time -> navController.navigate(R.id.screenTimeFragment)
                R.id.nav_app_control -> navController.navigate(R.id.appControlFragment)
                R.id.nav_contacts -> navController.navigate(R.id.contactsFragment)
                R.id.nav_location -> navController.navigate(R.id.locationFragment)
                R.id.nav_device_lock -> navController.navigate(R.id.deviceLockFragment)
            }
            drawerLayout.closeDrawers()
            true
        }
    }
}
