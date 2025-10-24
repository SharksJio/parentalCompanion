package com.parentalcompanion.parent.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parentalcompanion.parent.data.model.ContactControl
import com.parentalcompanion.parent.data.repository.ParentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ContactsViewModel : ViewModel() {
    
    private val repository = ParentRepository()
    
    private val _contacts = MutableStateFlow<List<ContactControl>>(emptyList())
    val contacts: StateFlow<List<ContactControl>> = _contacts
    
    fun loadContacts(deviceId: String) {
        viewModelScope.launch {
            repository.observeContacts(deviceId).collect { contactList ->
                _contacts.value = contactList
            }
        }
    }
    
    fun updateContact(deviceId: String, contact: ContactControl) {
        viewModelScope.launch {
            repository.updateContact(deviceId, contact)
        }
    }
}
