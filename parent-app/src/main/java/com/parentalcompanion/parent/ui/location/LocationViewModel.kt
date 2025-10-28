package com.parentalcompanion.parent.ui.location

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.Geofence
import com.parentalcompanion.parent.data.model.LocationData
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "LocationViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location
    
    private val _geofences = MutableStateFlow<List<Geofence>>(emptyList())
    val geofences: StateFlow<List<Geofence>> = _geofences
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun loadLocation(deviceId: String) {
        viewModelScope.launch {
            repository.observeLocation(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing location for device $deviceId", e)
                    _errorMessage.value = "Failed to load location: ${e.message}"
                }
                .collect { loc ->
                    _location.value = loc
                }
        }
    }
    
    fun loadGeofences(deviceId: String) {
        viewModelScope.launch {
            repository.observeGeofences(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing geofences for device $deviceId", e)
                    _errorMessage.value = "Failed to load geofences: ${e.message}"
                }
                .collect { fences ->
                    _geofences.value = fences
                }
        }
    }
    
    fun requestLocationUpdate(deviceId: String) {
        viewModelScope.launch {
            try {
                repository.requestLocationUpdate(deviceId)
            } catch (e: Exception) {
                Log.e(TAG, "Error requesting location update for device $deviceId", e)
                _errorMessage.value = "Failed to request location update: ${e.message}"
            }
        }
    }
    
    fun addGeofence(deviceId: String, geofence: Geofence) {
        viewModelScope.launch {
            try {
                repository.addGeofence(deviceId, geofence)
            } catch (e: Exception) {
                Log.e(TAG, "Error adding geofence for device $deviceId", e)
                _errorMessage.value = "Failed to add geofence: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
