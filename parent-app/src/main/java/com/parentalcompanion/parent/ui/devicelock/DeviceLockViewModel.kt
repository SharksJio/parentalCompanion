package com.parentalcompanion.parent.ui.devicelock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.launch

class DeviceLockViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    fun lockDevice(deviceId: String) {
        viewModelScope.launch {
            repository.updateDeviceLockStatus(deviceId, true)
        }
    }
    
    fun unlockDevice(deviceId: String) {
        viewModelScope.launch {
            repository.updateDeviceLockStatus(deviceId, false)
        }
    }
}
