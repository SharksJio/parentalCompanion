package com.parentalcompanion.parent.ui.devicelock

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ChildDevice
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DeviceLockViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "DeviceLockViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _childDevice = MutableStateFlow<ChildDevice?>(null)
    val childDevice: StateFlow<ChildDevice?> = _childDevice
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun loadChildDevice(deviceId: String) {
        viewModelScope.launch {
            repository.observeChildDevice(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing child device $deviceId", e)
                    _errorMessage.value = "Failed to load device: ${e.message}"
                }
                .collect { device ->
                    _childDevice.value = device
                }
        }
    }
    
    suspend fun toggleDeviceLock(deviceId: String) {
        try {
            val currentDevice = _childDevice.value
            if (currentDevice != null) {
                repository.updateDeviceLockStatus(deviceId, !currentDevice.isLocked)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error toggling device lock for device $deviceId", e)
            _errorMessage.value = "Failed to toggle device lock: ${e.message}"
            throw e
        }
    }
    
    fun lockDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                repository.updateDeviceLockStatus(deviceId, true)
            } catch (e: Exception) {
                Log.e(TAG, "Error locking device $deviceId", e)
                _errorMessage.value = "Failed to lock device: ${e.message}"
            }
        }
    }
    
    fun unlockDevice(deviceId: String) {
        viewModelScope.launch {
            try {
                repository.updateDeviceLockStatus(deviceId, false)
            } catch (e: Exception) {
                Log.e(TAG, "Error unlocking device $deviceId", e)
                _errorMessage.value = "Failed to unlock device: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
