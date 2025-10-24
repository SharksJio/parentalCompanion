package com.parentalcompanion.parent.ui.screentime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ScreenTimeLimit
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScreenTimeViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    private val _screenTime = MutableStateFlow<ScreenTimeLimit?>(null)
    val screenTime: StateFlow<ScreenTimeLimit?> = _screenTime
    
    fun loadScreenTime(deviceId: String) {
        viewModelScope.launch {
            repository.observeScreenTime(deviceId).collect { limit ->
                _screenTime.value = limit
            }
        }
    }
    
    fun setDailyLimit(deviceId: String, minutes: Int) {
        viewModelScope.launch {
            repository.setScreenTimeLimit(deviceId, minutes)
        }
    }
}
