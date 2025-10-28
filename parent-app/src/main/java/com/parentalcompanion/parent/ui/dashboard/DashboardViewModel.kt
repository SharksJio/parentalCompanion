package com.parentalcompanion.parent.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ChildDevice
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "DashboardViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _childDevice = MutableStateFlow<ChildDevice?>(null)
    val childDevice: StateFlow<ChildDevice?> = _childDevice
    
    private val _isConnectedToFirebase = MutableStateFlow(false)
    val isConnectedToFirebase: StateFlow<Boolean> = _isConnectedToFirebase
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    init {
        // Monitor Firebase connection state
        viewModelScope.launch {
            repository.observeConnectionState()
                .catch { e ->
                    Log.e(TAG, "Error observing connection state", e)
                    _errorMessage.value = "Failed to monitor connection: ${e.message}"
                }
                .collect { isConnected ->
                    _isConnectedToFirebase.value = isConnected
                }
        }
    }
    
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
    
    fun clearError() {
        _errorMessage.value = null
    }
}
