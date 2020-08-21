package com.example.android.trackmysleepquality.sleeptracker

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.ListItemSleepNightBinding
import com.example.android.trackmysleepquality.formatMillisForDuration
import com.example.android.trackmysleepquality.generated.callback.OnClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_HEADER = 0
private val ITEM_VIEW_SLEEP_ITEM = 1

class SleepNightAdapter(private val clickListener: SleepNightListener) :
      ListAdapter<DataItem, RecyclerView.ViewHolder>(SleepNightDiffCallback()) {

  override fun getItemViewType(position: Int): Int {
    return when(getItem(position)) {
      is DataItem.Header -> ITEM_VIEW_HEADER
      is DataItem.SleepNightItem -> ITEM_VIEW_SLEEP_ITEM
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is SleepViewHolder -> {
        val item = getItem(position) as DataItem.SleepNightItem
        holder.bind(item.sleepNight, clickListener)
      }
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      ITEM_VIEW_SLEEP_ITEM ->  SleepViewHolder.from(parent)
      ITEM_VIEW_HEADER -> TextViewHolder.from(parent)
      else -> throw ClassCastException("Unknown viewType $viewType")
    }
  }

  private val adapterScope = CoroutineScope(Dispatchers.Default)

  fun addHeaderAndSubmitList(list: List<SleepNight>?) {
    adapterScope.launch {
      val items = when (list) {
        null -> listOf(DataItem.Header)
        else -> listOf(DataItem.Header) + list.map { DataItem.SleepNightItem(it) }
      }
      withContext(Dispatchers.Main) {
        submitList(items)
      }
    }
  }

  class SleepViewHolder private constructor(val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SleepNight, clickListener: SleepNightListener) {
      binding.sleep = item;
      binding.clickListener = clickListener
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

  class TextViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    companion object {
      fun from(parent: ViewGroup): TextViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.header, parent, false)
        return TextViewHolder(view)
      }
    }
  }


}

class SleepNightDiffCallback : DiffUtil.ItemCallback<DataItem>() {
  override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
    return oldItem.id === newItem.id
  }

  override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
    return oldItem == newItem
  }
}

class SleepNightListener(val listener: (sleepId: Long) -> Unit) {

  fun onClick(night: SleepNight) { listener(night.nightId) }

}

sealed class DataItem {
  data class SleepNightItem(val sleepNight: SleepNight) : DataItem() {
    override val id = sleepNight.nightId
  }
  object Header : DataItem() {
    override val id = Long.MIN_VALUE
  }

  abstract val id:Long
}

