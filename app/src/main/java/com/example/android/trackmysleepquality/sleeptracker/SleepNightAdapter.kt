package com.example.android.trackmysleepquality.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.formatMillisForDuration

class SleepNightAdapter : ListAdapter<SleepNight, SleepNightAdapter.SleepViewHolder>(SleepNightDiffCallback()) {

  override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
    val item:SleepNight = getItem(position)
    holder.bind(item)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
    return SleepViewHolder.from(parent)
  }

  class SleepViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SleepNight) {
      binding.sleep = item;
      binding.executePendingBindings()
    }

    companion object {
      fun from(parent: ViewGroup): SleepViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ListItemSleepNightBinding.inflate(layoutInflater, parent, false)
        return SleepViewHolder(binding)
      }
    }
  }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
  override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
    return oldItem.nightId === newItem.nightId
  }

  override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
    return oldItem == newItem
  }
}
