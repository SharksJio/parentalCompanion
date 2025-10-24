package com.parentalcompanion.parent.ui.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.Geofence
import com.parentalcompanion.parent.data.model.LocationData
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location
    
    private val _geofences = MutableStateFlow<List<Geofence>>(emptyList())
    val geofences: StateFlow<List<Geofence>> = _geofences
    
    fun loadLocation(deviceId: String) {
        viewModelScope.launch {
            repository.observeLocation(deviceId).collect { loc ->
                _location.value = loc
            }
        }
    }
    
    fun loadGeofences(deviceId: String) {
        viewModelScope.launch {
            repository.observeGeofences(deviceId).collect { fences ->
                _geofences.value = fences
            }
        }
    }
    
    fun requestLocationUpdate(deviceId: String) {
        viewModelScope.launch {
            repository.requestLocationUpdate(deviceId)
        }
    }
    
    fun addGeofence(deviceId: String, geofence: Geofence) {
        viewModelScope.launch {
            repository.addGeofence(deviceId, geofence)
        }
    }
}
