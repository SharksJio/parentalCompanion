package com.parentalcompanion.parent.ui.appcontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.parentalcompanion.parent.databinding.FragmentAppControlBinding
import kotlinx.coroutines.launch

class AppControlFragment : Fragment() {
    
    private var _binding: FragmentAppControlBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AppControlViewModel
    private lateinit var adapter: AppControlAdapter
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppControlBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[AppControlViewModel::class.java]
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        
        // Setup RecyclerView with adapter
        adapter = AppControlAdapter(
            onBlockToggle = { app ->
                viewModel.updateApp(deviceId, app)
                Toast.makeText(
                    requireContext(),
                    if (app.isBlocked) "App blocked" else "App unblocked",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onTimeLimitSet = { app ->
                viewModel.updateApp(deviceId, app)
                val hours = app.dailyTimeLimit / 60
                val minutes = app.dailyTimeLimit % 60
                Toast.makeText(
                    requireContext(),
                    if (app.dailyTimeLimit > 0) "Time limit set to ${hours}h ${minutes}m" else "Time limit removed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        
        binding.rvApps.layoutManager = LinearLayoutManager(requireContext())
        binding.rvApps.adapter = adapter
        
        viewModel.loadAppControls(deviceId)
        
        // Observe app controls data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appControls.collect { apps ->
                if (apps.isEmpty()) {
                    // Show empty state
                    binding.rvApps.visibility = View.GONE
                } else {
                    binding.rvApps.visibility = View.VISIBLE
                    
                    // Filter apps based on selected tab
                    val selectedTab = binding.tabLayout.selectedTabPosition
                    val filteredApps = when (selectedTab) {
                        1 -> apps.filter { it.isBlocked } // Blocked apps
                        else -> apps.filter { !it.isBlocked } // Allowed apps
                    }
                    
                    adapter.submitList(filteredApps)
                }
            }
        }
        
        // Setup tab selection listener
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                // Trigger refiltering by collecting the current state
                viewLifecycleOwner.lifecycleScope.launch {
                    val apps = viewModel.appControls.value
                    val selectedTab = binding.tabLayout.selectedTabPosition
                    val filteredApps = when (selectedTab) {
                        1 -> apps.filter { it.isBlocked } // Blocked apps
                        else -> apps.filter { !it.isBlocked } // Allowed apps
                    }
                    adapter.submitList(filteredApps)
                }
            }
            
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
