package com.parentalcompanion.parent

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class ParentApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        
        // Enable Firebase Realtime Database persistence
        // This allows offline data access and syncs when online
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        
        // Enable Firebase Database logging for debugging (optional)
        // FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG)
    }
}
