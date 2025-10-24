package com.parentalcompanion.child.data.model

data class GeofenceData(
    val id: String = "",
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val radiusMeters: Float = 100f,
    val isActive: Boolean = true,
    val notifyOnEnter: Boolean = true,
    val notifyOnExit: Boolean = true
)
