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
import android.text.format.DateUtils
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
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    //TODO (01) Declare Job() and cancel jobs in onCleared().
    var viewModelJob = Job()

    //TODO (02) Define uiScope for coroutines.
    var uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    //TODO (03) Create a MutableLiveData variable tonight for one SleepNight.
    var tonight = MutableLiveData<SleepNight>()

    //TODO (04) Define a variable, nights. Then getAllNights() from the database
    //and assign to the nights variable.
    var nights = database.getAllNights()

    //TODO (05) In an init block, initializeTonight(), and implement it to launch a coroutine
    //to getTonightFromDatabase().

    //TODO (12) Transform nights into a nightsString using formatNights().
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)

    }
    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    //TODO (06) Implement getTonightFromDatabase()as a suspend function.
    private suspend fun getTonightFromDatabase(): SleepNight? {
        return withContext(Dispatchers.IO){
            var night = database.getTonight()
            if(night?.startTimeMilli != night?.endTimeMilli) {
                night = null
            }
            night
        }
    }

    //TODO (07) Implement the click handler for the Start button, onStartTracking(), using
    //coroutines. Define the suspend function insert(), to insert a new night into the database.
    fun onStartTracking() {
        uiScope.launch {
            insert()
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun insert() {
        withContext(Dispatchers.IO) {
            if(tonight.value == null ) {
                val newNight = SleepNight()
                database.insert(newNight)
            }
        }
    }

    //TODO (08) Create onStopTracking() for the Stop button with an update() suspend function.
    fun onStopTracking() {
        if (tonight.value != null) {
            uiScope.launch {
                val oldNight = tonight.value ?: return@launch
                oldNight.endTimeMilli = System.currentTimeMillis()
                update(oldNight)
            }
        }
    }

    //TODO (09) For the Clear button, created onClear() with a clear() suspend function.

    fun onClearButton() {
        uiScope.launch {
            withContext(Dispatchers.IO) {
                database.clear()
            }
        }
    }

    private suspend fun update(night: SleepNight){
        withContext(Dispatchers.IO) {
            database.update(night)
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}

