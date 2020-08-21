package com.example.android.trackmysleepquality.nightdetail

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.android.trackmysleepquality.R

class NightDetailFragment : Fragment() {

    companion object {
        fun newInstance() = NightDetailFragment()
    }

    private lateinit var viewModel: NightDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_night_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NightDetailViewModel::class.java)
        // TODO: Use the ViewModel
    }

}