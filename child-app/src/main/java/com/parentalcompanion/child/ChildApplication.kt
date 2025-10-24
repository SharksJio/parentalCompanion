package com.parentalcompanion.child

import android.app.Application
import com.google.firebase.FirebaseApp

class ChildApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
