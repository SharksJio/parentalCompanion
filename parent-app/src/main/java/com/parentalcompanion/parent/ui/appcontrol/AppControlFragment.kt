package com.parentalcompanion.parent.ui.appcontrol

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        
        // Setup RecyclerView
        binding.rvApps.layoutManager = LinearLayoutManager(requireContext())
        
        // TODO: Get device ID from SharedPreferences or login
        val deviceId = "test_device_id"
        viewModel.loadAppControls(deviceId)
        
        // Observe app controls data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appControls.collect { apps ->
                // TODO: Setup adapter to display apps
            }
        }
        
        // Setup tab selection (for allowed/blocked tabs)
        // TODO: Implement tab switching logic to filter apps
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
