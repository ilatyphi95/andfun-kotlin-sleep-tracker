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

package com.example.android.trackmysleepquality.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

// TODO (01) Create annotated interface SleepDatabaseDao.
@Dao
interface SleepDatabaseDao {

// TODO (02) Add annotated insert() method for inserting a single SleepNight.
    @Insert
    fun insert(sleepNight: SleepNight)

// TODO (03) Add annotated update() method for updating a SleepNight.
    @Update
    fun update(sleepNight: SleepNight)

// TODO (04) Add annotated get() method that gets the SleepNight by key.
    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :id")
    fun getNight(id : Long): LiveData<SleepNight>

// TODO (05) Add annotated clear() method and query.
    @Query("DELETE FROM daily_sleep_quality_table")
    fun deleteAllNight()

// TODO (06) Add annotated getAllNights() method and query.
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNight() : LiveData<List<SleepNight>>

// TODO (07) Add annotated getTonight() method and query.
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight() : LiveData<SleepNight>
}

