package com.parentalcompanion.parent.ui.appcontrol

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.AppControl
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppControlViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    private val _appControls = MutableStateFlow<List<AppControl>>(emptyList())
    val appControls: StateFlow<List<AppControl>> = _appControls
    
    fun loadAppControls(deviceId: String) {
        viewModelScope.launch {
            repository.observeAppControls(deviceId).collect { appList ->
                _appControls.value = appList
            }
        }
    }
    
    fun loadApps(deviceId: String) {
        viewModelScope.launch {
            repository.observeAppControls(deviceId).collect { appList ->
                _appControls.value = appList
            }
        }
    }
    
    fun updateApp(deviceId: String, appControl: AppControl) {
        viewModelScope.launch {
            repository.updateAppControl(deviceId, appControl)
        }
    }
}
