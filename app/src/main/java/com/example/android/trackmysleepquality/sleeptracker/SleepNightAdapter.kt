package com.example.android.trackmysleepquality.sleeptracker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.TextItemViewHolder
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
    val res = holder.itemView.context.resources
    holder.sleepLength.text = formatMillisForDuration(item.startTimeMillis, item.endTimeMillis)
    holder.quality.text = convertNumericQualityToString(item.sleepQuality, res)
    holder.image.setImageResource(when (item.sleepQuality) {
      0 -> R.drawable.ic_sleep_0
      1 -> R.drawable.ic_sleep_1
      2 -> R.drawable.ic_sleep_2
      3 -> R.drawable.ic_sleep_3
      4 -> R.drawable.ic_sleep_4
      5 -> R.drawable.ic_sleep_5
      else -> R.drawable.ic_sleep_active
    })
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    val view = layoutInflater.inflate(R.layout.list_item_sleep_night, parent, false)
    return SleepViewHolder(view)
  }

  class SleepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val sleepLength: TextView = itemView.findViewById(R.id.sleep_length)
    val quality:TextView = itemView.findViewById(R.id.quality_string)
    val image: ImageView = itemView.findViewById(R.id.quality_image)


  }
}