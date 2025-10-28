package com.parentalcompanion.parent.ui.appcontrol

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.parentalcompanion.parent.data.model.AppControl
import com.parentalcompanion.parent.databinding.ItemAppControlBinding

class AppControlAdapter(
    private val onBlockToggle: (AppControl) -> Unit,
    private val onTimeLimitSet: (AppControl) -> Unit
) : ListAdapter<AppControl, AppControlAdapter.ViewHolder>(AppControlDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppControlBinding.inflate(
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
        private val binding: ItemAppControlBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: AppControl) {
            binding.apply {
                tvAppName.text = app.appName
                tvPackageName.text = app.packageName
                switchBlocked.isChecked = app.isBlocked
                
                // Display usage and limit
                val usageHours = app.usedTimeToday / 60
                val usageMinutes = app.usedTimeToday % 60
                tvUsageTime.text = "${usageHours}h ${usageMinutes}m used"
                
                if (app.dailyTimeLimit > 0) {
                    val limitHours = app.dailyTimeLimit / 60
                    val limitMinutes = app.dailyTimeLimit % 60
                    tvTimeLimit.text = "Limit: ${limitHours}h ${limitMinutes}m"
                    etTimeLimit.setText(app.dailyTimeLimit.toString())
                } else {
                    tvTimeLimit.text = "No time limit"
                    etTimeLimit.setText("")
                }
                
                switchBlocked.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked != app.isBlocked) {
                        onBlockToggle(app.copy(isBlocked = isChecked))
                    }
                }
                
                btnSetTimeLimit.setOnClickListener {
                    val limitText = etTimeLimit.text.toString()
                    val limitMinutes = limitText.toIntOrNull() ?: 0
                    if (limitMinutes != app.dailyTimeLimit) {
                        onTimeLimitSet(app.copy(dailyTimeLimit = limitMinutes))
                    }
                }
            }
        }
    }
    
    class AppControlDiffCallback : DiffUtil.ItemCallback<AppControl>() {
        override fun areItemsTheSame(oldItem: AppControl, newItem: AppControl): Boolean {
            return oldItem.packageName == newItem.packageName
        }
        
        override fun areContentsTheSame(oldItem: AppControl, newItem: AppControl): Boolean {
            return oldItem == newItem
        }
    }
}
