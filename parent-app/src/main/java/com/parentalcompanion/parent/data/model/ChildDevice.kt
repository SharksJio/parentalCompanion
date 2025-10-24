package com.parentalcompanion.parent.data.model

data class ChildDevice(
    val deviceId: String = "",
    val deviceName: String = "",
    val childName: String = "",
    val isOnline: Boolean = false,
    val lastSeen: Long = 0L,
    val isLocked: Boolean = false
)
