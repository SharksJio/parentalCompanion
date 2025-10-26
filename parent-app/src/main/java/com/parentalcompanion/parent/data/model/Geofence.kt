package com.parentalcompanion.parent.data.model

data class Geofence(
    var id: String = "",
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var radiusMeters: Float = 100f,
    var isActive: Boolean = true,
    var notifyOnEnter: Boolean = true,
    var notifyOnExit: Boolean = true
)
