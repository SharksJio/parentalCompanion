package com.parentalcompanion.parent.ui.location

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.parentalcompanion.parent.databinding.FragmentLocationBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class LocationFragment : Fragment() {
    
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: LocationViewModel
    
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadLocationData()
        } else {
            Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[LocationViewModel::class.java]
        
        // Setup RecyclerView for geofences
        binding.rvGeofences.layoutManager = LinearLayoutManager(requireContext())
        
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Check for location permission
        checkLocationPermission()
        
        // Setup UI listeners
        binding.btnRequestUpdate.setOnClickListener {
            requestLocationUpdate()
        }
        
        binding.fabAddGeofence.setOnClickListener {
            // TODO: Open dialog to add geofence
            Toast.makeText(requireContext(), "Add geofence feature coming soon", Toast.LENGTH_SHORT).show()
        }
        
        // Observe location data
        observeLocationData()
    }
    
    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                loadLocationData()
            }
            else -> {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    
    private fun loadLocationData() {
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadLocation(deviceId)
        viewModel.loadGeofences(deviceId)
    }
    
    private fun observeLocationData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.location.collect { location ->
                if (location != null) {
                    binding.tvLatitude.text = String.format("%.6f", location.latitude)
                    binding.tvLongitude.text = String.format("%.6f", location.longitude)
                    
                    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
                    binding.tvLastUpdated.text = dateFormat.format(Date(location.timestamp))
                } else {
                    binding.tvLatitude.text = "-"
                    binding.tvLongitude.text = "-"
                    binding.tvLastUpdated.text = "-"
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.geofences.collect { geofences ->
                // TODO: Setup geofence adapter and display list
            }
        }
    }
    
    private fun requestLocationUpdate() {
        val deviceId = "test_device_id"
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.requestLocationUpdate(deviceId)
            Toast.makeText(requireContext(), "Location update requested", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
