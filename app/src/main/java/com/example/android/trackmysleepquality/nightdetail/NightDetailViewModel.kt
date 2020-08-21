package com.example.android.trackmysleepquality.nightdetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

class NightDetailViewModel(
      private val dao: SleepDatabaseDao,
      private val sleepId: Long) : ViewModel() {

  private val _sleepNight = MutableLiveData<SleepNight>()
  val sleepNight:LiveData<SleepNight>
    get() = _sleepNight

  private val viewModelJob = Job()
  private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

  init {
    initializeSleepNight()
  }

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  private fun initializeSleepNight() {
    Log.w("NightDetailViewModel", ">>> initializeSleepNight")
    uiScope.launch {
      _sleepNight.value = getNight()
    }
  }
  private suspend fun getNight(): SleepNight? {
    return withContext(Dispatchers.IO) {
      var night = dao.get(sleepId)
      night
    }
  }

}