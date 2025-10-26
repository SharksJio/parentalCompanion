package com.parentalcompanion.parent.ui.devicelock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.parentalcompanion.parent.R
import com.parentalcompanion.parent.databinding.FragmentDeviceLockBinding
import kotlinx.coroutines.launch

class DeviceLockFragment : Fragment() {
    
    private var _binding: FragmentDeviceLockBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DeviceLockViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceLockBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[DeviceLockViewModel::class.java]
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadChildDevice(deviceId)
        
        // Observe lock status
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.childDevice.collect { device ->
                if (device != null) {
                    updateLockStatus(device.isLocked)
                }
            }
        }
        
        // Setup button click listener
        binding.btnToggleLock.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.toggleDeviceLock(deviceId)
                Toast.makeText(requireContext(), "Lock status updated", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun updateLockStatus(isLocked: Boolean) {
        if (isLocked) {
            binding.tvLockStatus.text = getString(R.string.device_locked)
            binding.tvLockStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark))
            binding.btnToggleLock.text = getString(R.string.unlock_device)
        } else {
            binding.tvLockStatus.text = getString(R.string.device_unlocked)
            binding.tvLockStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark))
            binding.btnToggleLock.text = getString(R.string.lock_device)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
