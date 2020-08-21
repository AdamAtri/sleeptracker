package com.example.android.trackmysleepquality.nightdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import java.lang.IllegalArgumentException

class NightDetailViewModelFactory(
      val dao:SleepDatabaseDao,
      val sleepId: Long) : ViewModelProvider.Factory {

  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    if (modelClass.isAssignableFrom(NightDetailViewModel::class.java)) {
      return NightDetailViewModel(dao, sleepId) as T
    }
    throw IllegalArgumentException("Cannot create view model::NDVMF")
  }
}