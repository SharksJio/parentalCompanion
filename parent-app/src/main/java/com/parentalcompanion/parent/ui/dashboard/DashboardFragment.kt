package com.parentalcompanion.parent.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.parentalcompanion.parent.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {
    
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
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
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
