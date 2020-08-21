package com.example.android.trackmysleepquality.nightdetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.FragmentNightDetailBinding

class NightDetailFragment : Fragment() {


  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {

    Log.w("NightDetailFragment", ">>> onCreate ")

    val binding: FragmentNightDetailBinding = DataBindingUtil.inflate(
      inflater, R.layout.fragment_night_detail, container, false)
    binding.setLifecycleOwner(this)

    val args = NightDetailFragmentArgs.fromBundle(arguments!!)
    val application = requireNotNull(activity).application
    val dao = SleepDatabase.getInstance(application.applicationContext).sleepDatabaseDao
    val factory = NightDetailViewModelFactory(dao, args.sleepNightKey)
    val model = ViewModelProviders.of(this, factory).get(NightDetailViewModel::class.java)

    binding.viewModel = model

    return binding.root
  }

}