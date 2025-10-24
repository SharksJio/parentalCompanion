package com.parentalcompanion.child.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.child.data.repository.ChildRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val repository = ChildRepository()
    
    fun updateDeviceStatus(deviceId: String, isOnline: Boolean) {
        viewModelScope.launch {
            repository.updateDeviceStatus(deviceId, isOnline)
        }
    }
}
