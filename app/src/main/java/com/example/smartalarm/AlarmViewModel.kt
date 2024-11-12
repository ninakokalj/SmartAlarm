package com.example.smartalarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmDao: AlarmDao = AlarmDatabase.getDatabase(application).alarmDao()

    // LiveData that holds the list of alarms, loaded asynchronously
    val allAlarms: LiveData<List<AlarmEntity>> = alarmDao.getAllAlarms().asLiveData()

    // Function to add an alarm
    fun addAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.insertAlarm(alarm)
        }
    }

    // Function to update an alarm
    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.updateAlarm(alarm)
        }
    }

    // Function to delete an alarm
    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            alarmDao.deleteAlarm(alarm)
        }
    }
}
