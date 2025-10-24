package com.parentalcompanion.parent.ui.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.parentalcompanion.parent.R

class ContactsFragment : Fragment() {
    
    private lateinit var viewModel: ContactsViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this)[ContactsViewModel::class.java]
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }
}
