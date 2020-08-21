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

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(val database: SleepDatabaseDao, application: Application) : AndroidViewModel(application) {

  private var viewModelJob = Job()

  override fun onCleared() {
    super.onCleared()
    viewModelJob.cancel()
  }

  // provide scope for the coroutine to run in
  private val jobScope = CoroutineScope(Dispatchers.Main + viewModelJob)
  private var tonight = MutableLiveData<SleepNight?>()
  val nights = database.getAllNights()

  val nightString = Transformations.map(nights) {nights ->
    formatNights(nights, application.resources)
  }

  private var _navToQualityEvent = MutableLiveData<SleepNight>()
  val navToQualityEvent: LiveData<SleepNight>
    get() = _navToQualityEvent

  fun doneNavigating() {
    _navToQualityEvent.value = null
  }

  init {
    initializeTonight()
  }

  private fun initializeTonight() {
    jobScope.launch {
      tonight.value = getTonight()
    }
  }

  private suspend fun getTonight(): SleepNight? {
    return withContext(Dispatchers.IO) {
      var night = database.getTonight()
      if (night?.endTimeMillis != night?.startTimeMillis)
        night = null
      night
    }
  }

  fun onStartTracking() {
    jobScope.launch {
      val newNight = SleepNight()
      insert(newNight)
      tonight.value = getTonight()
    }
  }

  private suspend fun insert(night:SleepNight) {
    withContext(Dispatchers.IO) {
      database.insert(night)
    }
  }

  fun onStopTracking() {
    jobScope.launch {
      val oldNight = tonight.value ?: return@launch
      oldNight.endTimeMillis = System.currentTimeMillis()
      update(oldNight)
      _navToQualityEvent.value = oldNight
    }
  }

  private suspend fun update(night:SleepNight) {
    withContext(Dispatchers.IO) {
      database.update(night)
    }
  }

  fun onClear() {
    jobScope.launch {
      clear()
      _showSnackEvent.value = true;
    }
  }

  private suspend fun clear() {
    withContext(Dispatchers.IO) {
      database.clear()
    }
  }

  val startButtonVisible = Transformations.map(tonight) {
    it == null
  }
  val stopButtonVisible = Transformations.map(tonight) {
    it != null
  }
  val clearButtonVisible = Transformations.map(nights) {
    it?.isNotEmpty()
  }

  private var _showSnackEvent = MutableLiveData<Boolean>()
  val showSnackEvent: LiveData<Boolean>
    get() = _showSnackEvent

  fun doneShowingSnack() {
    _showSnackEvent.value = false
  }

  private val _navToDetailEvent = MutableLiveData<Long>()
  val navToDetailEvent: LiveData<Long>
    get() = _navToDetailEvent

  fun showDetailForId(sleepId:Long) {
    _navToDetailEvent.value = sleepId
  }

  fun doneNavigatingToDetail() {
    _navToDetailEvent.value = -1
  }
}

