package com.parentalcompanion.parent.ui.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parentalcompanion.parent.data.model.QuickAction
import com.parentalcompanion.parent.databinding.ItemQuickActionBinding

class QuickActionAdapter(
    private val onItemClick: (QuickAction) -> Unit
) : ListAdapter<QuickAction, QuickActionAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemQuickActionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemQuickActionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(action: QuickAction) {
            binding.tvTitle.text = action.title
            binding.tvDescription.text = action.description
            binding.ivIcon.setImageResource(action.iconRes)
            binding.root.setOnClickListener {
                onItemClick(action)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<QuickAction>() {
            override fun areItemsTheSame(oldItem: QuickAction, newItem: QuickAction): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: QuickAction, newItem: QuickAction): Boolean {
                return oldItem == newItem
            }
        }
    }
}
