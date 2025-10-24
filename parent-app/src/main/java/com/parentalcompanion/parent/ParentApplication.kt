package com.parentalcompanion.parent

import android.app.Application
import com.google.firebase.FirebaseApp

class ParentApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
