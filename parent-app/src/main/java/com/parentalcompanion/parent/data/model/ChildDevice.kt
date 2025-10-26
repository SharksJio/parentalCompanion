package com.parentalcompanion.parent.data.model

import com.google.firebase.database.PropertyName

data class ChildDevice(
    var deviceId: String = "",
    var deviceName: String = "",
    var childName: String = "",
    @get:PropertyName("isOnline")
    @set:PropertyName("isOnline")
    var isOnline: Boolean = false,
    var lastSeen: Long = 0L,
    var isLocked: Boolean = false
)
