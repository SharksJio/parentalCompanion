package com.parentalcompanion.parent.data.model

data class ScreenTimeLimit(
    val deviceId: String = "",
    val dailyLimitMinutes: Int = 0,
    val usedMinutesToday: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
