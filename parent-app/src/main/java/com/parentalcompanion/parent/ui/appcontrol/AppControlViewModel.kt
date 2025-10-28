package com.parentalcompanion.parent.ui.appcontrol

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.AppControl
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class AppControlViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "AppControlViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _appControls = MutableStateFlow<List<AppControl>>(emptyList())
    val appControls: StateFlow<List<AppControl>> = _appControls
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun loadAppControls(deviceId: String) {
        viewModelScope.launch {
            repository.observeAppControls(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing app controls for device $deviceId", e)
                    _errorMessage.value = "Failed to load app controls: ${e.message}"
                }
                .collect { appList ->
                    _appControls.value = appList
                }
        }
    }
    
    fun loadApps(deviceId: String) {
        viewModelScope.launch {
            repository.observeAppControls(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing apps for device $deviceId", e)
                    _errorMessage.value = "Failed to load apps: ${e.message}"
                }
                .collect { appList ->
                    _appControls.value = appList
                }
        }
    }
    
    fun updateApp(deviceId: String, appControl: AppControl) {
        viewModelScope.launch {
            try {
                repository.updateAppControl(deviceId, appControl)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating app control for device $deviceId", e)
                _errorMessage.value = "Failed to update app: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
