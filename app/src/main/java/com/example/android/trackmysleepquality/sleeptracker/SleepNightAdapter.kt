package com.example.android.trackmysleepquality.sleeptracker

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.convertNumericQualityToString
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatMillisForDuration

class SleepNightAdapter : RecyclerView.Adapter<SleepNightAdapter.SleepViewHolder>() {

  var data = listOf<SleepNight>()
    set(value) {
      field = value
      notifyDataSetChanged()
    }

  override fun getItemCount() = data.size

  override fun onBindViewHolder(holder: SleepViewHolder, position: Int) {
    val item:SleepNight = data[position]
    holder.bind(item)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
    return SleepViewHolder.from(parent)
  }

  class SleepViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
    val quality:TextView = itemView.findViewById(R.id.quality_string)
    val image: ImageView = itemView.findViewById(R.id.quality_image)

    fun bind(item: SleepNight) {
      val res = itemView.context.resources
      sleepLength.text = formatMillisForDuration(item.startTimeMillis, item.endTimeMillis)
      quality.text = convertNumericQualityToString(item.sleepQuality, res)
      image.setImageResource(when (item.sleepQuality) {
        0 -> R.drawable.ic_sleep_0
        1 -> R.drawable.ic_sleep_1
        2 -> R.drawable.ic_sleep_2
        3 -> R.drawable.ic_sleep_3
        4 -> R.drawable.ic_sleep_4
        5 -> R.drawable.ic_sleep_5
        else -> R.drawable.ic_sleep_active
      })
    }

    companion object {
      fun from(parent: ViewGroup): SleepViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
        return SleepViewHolder(view)
      }
    }
  }


}