package com.parentalcompanion.parent.ui.screentime

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.parentalcompanion.parent.databinding.FragmentScreenTimeBinding
import kotlinx.coroutines.launch

class ScreenTimeFragment : Fragment() {
    
    private var _binding: FragmentScreenTimeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScreenTimeViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreenTimeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ScreenTimeViewModel::class.java]
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadScreenTime(deviceId)
        
        // Observe screen time data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.screenTimeLimit.collect { screenTime ->
                if (screenTime != null) {
                    val usageMinutes = screenTime.usedMinutes
                    val limitMinutes = screenTime.dailyLimitMinutes
                    
                    val hours = usageMinutes / 60
                    val minutes = usageMinutes % 60
                    binding.tvUsageTime.text = "${hours}h ${minutes}min"
                    
                    val remainingMinutes = limitMinutes - usageMinutes
                    if (remainingMinutes > 0) {
                        val remHours = remainingMinutes / 60
                        val remMinutes = remainingMinutes % 60
                        binding.tvTimeRemaining.text = "Time Remaining: ${remHours}h ${remMinutes}min"
                    } else {
                        binding.tvTimeRemaining.text = "Daily limit exceeded"
                    }
                    
                    binding.etDailyLimit.setText(limitMinutes.toString())
                }
            }
        }
        
        // Setup button click listener
        binding.btnSetLimit.setOnClickListener {
            val limitText = binding.etDailyLimit.text.toString()
            if (limitText.isNotEmpty()) {
                val limitMinutes = limitText.toIntOrNull()
                if (limitMinutes != null && limitMinutes > 0) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.setScreenTimeLimit(deviceId, limitMinutes)
                        Toast.makeText(requireContext(), "Screen time limit updated", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid limit", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
