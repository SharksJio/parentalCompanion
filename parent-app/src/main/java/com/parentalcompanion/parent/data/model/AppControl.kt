package com.parentalcompanion.parent.data.model

data class AppControl(
    val packageName: String = "",
    val appName: String = "",
    val isBlocked: Boolean = false,
    val dailyTimeLimit: Int = 0,
    val usedTimeToday: Int = 0
)
