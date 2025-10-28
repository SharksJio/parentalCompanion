package com.parentalcompanion.parent.ui.contacts

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ContactControl
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {
    
    companion object {
        private const val TAG = "ContactsViewModel"
    }
    
    private val repository = ParentRepository()
    
    private val _contacts = MutableStateFlow<List<ContactControl>>(emptyList())
    val contacts: StateFlow<List<ContactControl>> = _contacts
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage
    
    fun loadContacts(deviceId: String) {
        viewModelScope.launch {
            repository.observeContacts(deviceId)
                .catch { e ->
                    Log.e(TAG, "Error observing contacts for device $deviceId", e)
                    _errorMessage.value = "Failed to load contacts: ${e.message}"
                }
                .collect { contactList ->
                    _contacts.value = contactList
                }
        }
    }
    
    fun updateContact(deviceId: String, contact: ContactControl) {
        viewModelScope.launch {
            try {
                repository.updateContact(deviceId, contact)
            } catch (e: Exception) {
                Log.e(TAG, "Error updating contact for device $deviceId", e)
                _errorMessage.value = "Failed to update contact: ${e.message}"
            }
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
