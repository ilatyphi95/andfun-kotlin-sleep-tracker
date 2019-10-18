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

package com.example.android.trackmysleepquality.sleepquality

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.*

//TODO (03) Using the code in SleepTrackerViewModel for reference, create SleepQualityViewModel
//with coroutine setup and navigation setup.
class SleepQualityViewModel(val database: SleepDatabaseDao,
                            val sleepId : Long, application : Application) : AndroidViewModel(application) {
    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    private val night = MutableLiveData<SleepNight>()

    private val _onSetSleepQuality = MutableLiveData<Boolean>()
    val onSetSleepQuality : LiveData<Boolean>
            get() = _onSetSleepQuality
    init {
        getSleepNight();
    }

    private fun getSleepNight() {
        uiScope.launch {
            night.value = getThisNight()
        }
    }

    private suspend fun getThisNight() : SleepNight {
        return withContext(Dispatchers.IO) {
            var currentNight : SleepNight = SleepNight()
            currentNight = database.get(sleepId)?: SleepNight()
            currentNight
        }
    }

    override fun onCleared() {
        super.onCleared()
        uiScope.cancel()
    }

    //TODO (04) implement the onSetSleepQuality() click handler using coroutines.
    fun setSleepQuality(value: Int) {
        night.value?.sleepQuality = value
        uiScope.launch {
            insert()
        }
        _onSetSleepQuality.value = true
    }

    private suspend fun insert() {
        withContext(Dispatchers.IO) {
            if(night.value != null) {
                database.update(night.value!!)
            }
        }
    }

    fun doneNavigating() {
        _onSetSleepQuality.value = false
    }

}

