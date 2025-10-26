package com.parentalcompanion.parent.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.parentalcompanion.parent.R
import com.parentalcompanion.parent.data.model.QuickAction
import com.parentalcompanion.parent.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var quickActionAdapter: QuickActionAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        
        // Initialize RecyclerView adapter and layout manager before layout
        quickActionAdapter = QuickActionAdapter { action ->
            handleQuickAction(action)
        }
        binding.rvQuickActions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = quickActionAdapter
            setHasFixedSize(true)
        }
        
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Setup quick actions
        setupQuickActions()
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadChildDevice(deviceId)
        
        // Observe child device status
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.childDevice.collect { device ->
                if (device != null) {
                    binding.tvDeviceStatus.text = if (device.isOnline) "Online" else "Offline"
                } else {
                    binding.tvDeviceStatus.text = "No device registered"
                }
            }
        }
        
        // Observe Firebase connection state for debugging
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isConnectedToFirebase.collect { isConnected ->
                // You can add visual feedback here if needed
                // For example, show a toast or update UI when connection changes
            }
        }
    }
    
    private fun setupQuickActions() {
        val quickActions = listOf(
            QuickAction(
                id = "lock_device",
                title = "Lock Device",
                description = "Lock or unlock child device",
                iconRes = android.R.drawable.ic_lock_lock
            ),
            QuickAction(
                id = "screen_time",
                title = "Screen Time",
                description = "Manage screen time limits",
                iconRes = android.R.drawable.ic_menu_recent_history
            ),
            QuickAction(
                id = "app_control",
                title = "App Control",
                description = "Control app access",
                iconRes = android.R.drawable.ic_menu_manage
            ),
            QuickAction(
                id = "location",
                title = "Location",
                description = "View device location",
                iconRes = android.R.drawable.ic_menu_mylocation
            ),
            QuickAction(
                id = "contacts",
                title = "Contacts",
                description = "Manage allowed contacts",
                iconRes = android.R.drawable.ic_menu_my_calendar
            )
        )
        quickActionAdapter.submitList(quickActions)
    }
    
    private fun handleQuickAction(action: QuickAction) {
        when (action.id) {
            "lock_device" -> findNavController().navigate(R.id.deviceLockFragment)
            "screen_time" -> findNavController().navigate(R.id.screenTimeFragment)
            "app_control" -> findNavController().navigate(R.id.appControlFragment)
            "location" -> findNavController().navigate(R.id.locationFragment)
            "contacts" -> findNavController().navigate(R.id.contactsFragment)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
