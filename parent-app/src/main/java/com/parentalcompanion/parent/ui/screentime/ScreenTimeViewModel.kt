package com.parentalcompanion.parent.ui.screentime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ScreenTimeLimit
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScreenTimeViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "ScreenTimeViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _screenTimeLimit = MutableStateFlow<ScreenTimeLimit?>(null)
    val screenTimeLimit: StateFlow<ScreenTimeLimit?> = _screenTimeLimit
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun loadScreenTime(deviceId: String) {
        viewModelScope.launch {
            repository.observeScreenTime(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing screen time for device $deviceId", e)
                    _errorMessage.value = "Failed to load screen time: ${e.message}"
                }
                .collect { limit ->
                    _screenTimeLimit.value = limit
                }
        }
    }
    
    suspend fun setScreenTimeLimit(deviceId: String, minutes: Int) {
        try {
            repository.setScreenTimeLimit(deviceId, minutes)
        } catch (e: Exception) {
            Log.e(TAG, "Error setting screen time limit for device $deviceId", e)
            _errorMessage.value = "Failed to set screen time limit: ${e.message}"
            throw e
        }
    }
    
    fun setDailyLimit(deviceId: String, minutes: Int) {
        viewModelScope.launch {
            try {
                repository.setScreenTimeLimit(deviceId, minutes)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting daily limit for device $deviceId", e)
                _errorMessage.value = "Failed to set daily limit: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
