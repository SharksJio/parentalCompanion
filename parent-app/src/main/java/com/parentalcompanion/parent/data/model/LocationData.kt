package com.parentalcompanion.parent.data.model

data class LocationData(
    var deviceId: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var timestamp: Long = System.currentTimeMillis(),
    var accuracy: Float = 0f
)
