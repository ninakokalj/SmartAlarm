package com.example.smartalarm

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmEntity>> // Fetch all alarms

    @Query("SELECT * FROM alarms WHERE isEnabled = 1")  // Fetch enabled alarms
    fun getEnabledAlarms(): Flow<List<AlarmEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarm(alarm: AlarmEntity): Long // Insert an alarm

    @Update
    fun updateAlarm(alarm: AlarmEntity) // Update an alarm

    @Delete
    fun deleteAlarm(alarm: AlarmEntity) // Delete an alarm
}
