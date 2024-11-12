package com.example.smartalarm

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<AlarmEntity>> // Fetch all alarms

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAlarm(alarm: AlarmEntity) // Insert an alarm

    @Update
    fun updateAlarm(alarm: AlarmEntity) // Update an alarm

    @Delete
    fun deleteAlarm(alarm: AlarmEntity) // Delete an alarm
}
