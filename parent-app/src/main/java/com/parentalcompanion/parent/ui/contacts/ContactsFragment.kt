package com.parentalcompanion.parent.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.parentalcompanion.parent.databinding.FragmentContactsBinding
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {
    
    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ContactsViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup RecyclerView
        binding.rvContacts.layoutManager = LinearLayoutManager(requireContext())
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadContacts(deviceId)
        
        // Observe contacts data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                // TODO: Setup adapter to display contacts
            }
        }
        
        // Setup FAB click listener
        binding.fabAddContact.setOnClickListener {
            // TODO: Open dialog to add contact
            Toast.makeText(requireContext(), "Add contact feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Setup tab selection (for allowed/blocked tabs)
        // TODO: Implement tab switching logic to filter contacts
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
