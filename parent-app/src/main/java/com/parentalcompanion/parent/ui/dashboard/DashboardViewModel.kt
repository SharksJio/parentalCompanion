package com.parentalcompanion.parent.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ChildDevice
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    private val _childDevice = MutableStateFlow<ChildDevice?>(null)
    val childDevice: StateFlow<ChildDevice?> = _childDevice
    
    fun loadChildDevice(deviceId: String) {
        viewModelScope.launch {
            repository.observeChildDevice(deviceId).collect { device ->
                _childDevice.value = device
            }
        }
    }
}
