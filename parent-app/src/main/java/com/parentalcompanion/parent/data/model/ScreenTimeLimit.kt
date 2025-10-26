package com.parentalcompanion.parent.data.model

data class ScreenTimeLimit(
    var deviceId: String = "",
    var dailyLimitMinutes: Int = 0,
    var usedMinutesToday: Int = 0,
    var lastUpdated: Long = System.currentTimeMillis()
)
