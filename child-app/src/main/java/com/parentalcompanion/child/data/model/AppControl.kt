package com.parentalcompanion.child.data.model

data class AppControl(
    var packageName: String = "",
    var appName: String = "",
    var isBlocked: Boolean = false,
    var dailyTimeLimit: Int = 0,
    var usedTimeToday: Int = 0
)
