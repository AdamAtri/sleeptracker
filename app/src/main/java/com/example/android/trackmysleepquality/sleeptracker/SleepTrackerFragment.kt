/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao
        val trackerModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val sleepTrackerModel =
          ViewModelProviders.of(this, trackerModelFactory).get(SleepTrackerViewModel::class.java)

        // attach the model to the binding
        binding.sleepTrackerModel = sleepTrackerModel
        binding.setLifecycleOwner(this)

        // create a SleepNight list-adapter and attach to the binding
        val adapter = SleepNightAdapter(SleepNightListener { sleepId ->
            Toast.makeText(context, "SleepId $sleepId", Toast.LENGTH_SHORT).show()
            sleepTrackerModel.showDetailForId(sleepId)
        })
        binding.sleepList.adapter = adapter

        // create a GridLayoutManger and attach to the binding
        // columns = 3
        val manager = GridLayoutManager(activity, 3)
        manager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when(position) {
                0 -> 3
                else -> 1
            }
        }
        binding.sleepList.layoutManager = manager

        // add observers
        addModelObservers(sleepTrackerModel, adapter)

        // return the binding's root view
        return binding.root
    }

    /**
     * add observers to the sleepTrackerModel
    */
    private fun addModelObservers(sleepTrackerModel: SleepTrackerViewModel, adapter: SleepNightAdapter) {
        sleepTrackerModel.nights.observe(viewLifecycleOwner, Observer { nights ->
            nights?.let {
                adapter.addHeaderAndSubmitList(it)
            }
        })
        sleepTrackerModel.navToQualityEvent.observe(this, Observer { night ->
            // ---> <var>?.let{ } === whenNotNull(it:<VarClass> -> {...}) // (Elvis-operator and let)
            night?.let {
                this.findNavController().navigate(
                  SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                sleepTrackerModel.doneNavigating()
            }
        })
        sleepTrackerModel.showSnackEvent.observe(this, Observer { event ->
            event?.let {
                if (it) {
                    Snackbar.make(
                      activity!!.findViewById(android.R.id.content),
                      getString(R.string.cleared_message),
                      Snackbar.LENGTH_SHORT
                    ).show()
                    sleepTrackerModel.doneShowingSnack()
                }
            }
        })
        sleepTrackerModel.navToDetailEvent.observe(this, Observer { sleepId ->
            sleepId?.let {
                if (it > 0) {
                    this.findNavController().navigate(
                      SleepTrackerFragmentDirections.actionSleepTrackerFragmentToNightDetailFragment(it)
                    )
                    sleepTrackerModel.doneNavigatingToDetail()
                }
            }
        })
    }
}
