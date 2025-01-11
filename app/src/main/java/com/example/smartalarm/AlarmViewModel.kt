package com.example.smartalarm

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi


class AlarmViewModel(application: Application) : AndroidViewModel(application) {

    private val alarmDao: AlarmDao = AlarmDatabase.getDatabase(application).alarmDao()

    // LiveData that holds the list of alarms, loaded asynchronously
    val allAlarms: LiveData<List<AlarmEntity>> = alarmDao.getAllAlarms().asLiveData()

    // Function to add an alarm
    @RequiresApi(Build.VERSION_CODES.S)
    fun addAlarm(context: Context, alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val generatedId = alarmDao.insertAlarm(alarm)

            val updatedAlarm = alarm.copy(id = generatedId.toInt())
            scheduleAlarm(context, updatedAlarm)
        }
    }

    // Function to update an alarm
    @RequiresApi(Build.VERSION_CODES.S)
    fun updateAlarm(context: Context, alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            unscheduleAlarm(context, alarm)
            alarmDao.updateAlarm(alarm)
            if (alarm.isEnabled) {
                scheduleAlarm(context, alarm)
            }
        }
    }

    // Function to delete an alarm
    fun deleteAlarm(context: Context, alarm: AlarmEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (alarm.isEnabled) {
                unscheduleAlarm(context, alarm)
            }
            alarmDao.deleteAlarm(alarm)
        }
    }

    val daysOfWeek = mapOf(
        "Sunday" to Calendar.SUNDAY,
        "Monday" to Calendar.MONDAY,
        "Tuesday" to Calendar.TUESDAY,
        "Wednesday" to Calendar.WEDNESDAY,
        "Thursday" to Calendar.THURSDAY,
        "Friday" to Calendar.FRIDAY,
        "Saturday" to Calendar.SATURDAY
    )

    @RequiresApi(Build.VERSION_CODES.S)
    fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Check if the app can schedule exact alarms
        if (!alarmManager.canScheduleExactAlarms()) {
            // Redirect the user to the exact alarm permission screen
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                data = Uri.parse("package:${context.packageName}")
            }
            context.startActivity(intent)
            return
        }
        Log.d("Sch alarm", "Sch alarm with id ${alarm.id}")

        // Parse time
        val parts = alarm.time.split(":")
        val hour = parts[0].toInt()
        val min = parts[1].toInt()

        if (alarm.repeatDays.isEmpty()) {
            // Schedule a one-time alarm (next available time)
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, min)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)

                if (before(Calendar.getInstance())) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }
            val intent = createAlarmIntent(context, alarm)
            val pendingIntent = createPendingIntent(context, alarm, intent)

            // Log details of the Intent and PendingIntent
            Log.d("scheduleAlarm", "Scheduling alarm with details:")
            logIntentDetails("scheduleAlarm", intent)
            logPendingIntentDetails("scheduleAlarm", pendingIntent)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("Alarm", "Scheduled one-time alarm for ${calendar.time}")

        } else {
            // Schedule an alarm for every day in alarm.repeatDays
            alarm.repeatDays.forEach{ day ->
                daysOfWeek[day]?.let { dayOfWeek ->
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, min)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    var daysUntil = (dayOfWeek - today + 7) % 7

                    if (daysUntil == 0 && calendar.before(today)) {
                        daysUntil += 7
                    }
                    calendar.add(Calendar.DAY_OF_YEAR, daysUntil)

                    val code = alarm.id * 100 + dayOfWeek
                    val intent = createAlarmIntent(context, alarm)
                    val pendingIntent = createPendingIntent(context, alarm, intent, code)

                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("Alarm", "Scheduled weekly alarm for $day at ${calendar.time}")
                }
            }
        }
    }

    fun unscheduleAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = createAlarmIntent(context, alarm)

        if (alarm.repeatDays.isEmpty()) {
            val pendingIntent = createPendingIntent(context, alarm, intent)
            alarmManager.cancel(pendingIntent)
            Log.d("unscheduleAlarm", "unsch alarm with ID: ${alarm.id}")
        } else {
            alarm.repeatDays.forEach{ day ->
                daysOfWeek[day]?.let { dayOfWeek ->
                    val code = alarm.id * 100 + dayOfWeek
                    val pendingIntent = createPendingIntent(context, alarm, intent, code)
                    alarmManager.cancel(pendingIntent)
                    Log.d("unscheduleAlarm", "code of alarm: $code")
                    Log.d("unscheduleAlarm", "Unscheduled alarm with details:")
                    logIntentDetails("unscheduleAlarm", intent)
                    logPendingIntentDetails("unscheduleAlarm", pendingIntent)
                }

            }

        }
    }

    private fun createAlarmIntent(context: Context, alarm: AlarmEntity): Intent {
        return Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_LABEL", alarm.label)
            putExtra("ALARM_SOUND", alarm.sound)
            putExtra("ALARM_MISSION", alarm.mission)
            putExtra("REPEAT", !alarm.repeatDays.isEmpty())
        }
    }

    private fun createPendingIntent(
        context: Context,
        alarm: AlarmEntity,
        intent: Intent,
        requestCode: Int = alarm.id
    ): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun logIntentDetails(tag: String, intent: Intent) {
        Log.d(tag, "Intent details:")
        Log.d(tag, "  Action: ${intent.action}")
        Log.d(tag, "  Component: ${intent.component}")
        Log.d(tag, "  Extras: ${intent.extras?.toString() ?: "No extras"}")
    }

    private fun logPendingIntentDetails(tag: String, pendingIntent: PendingIntent) {
        Log.d(tag, "PendingIntent details:")
        Log.d(tag, "  Creator package: ${pendingIntent.creatorPackage}")
        Log.d(tag, "  Request code: ${pendingIntent.creatorUid}")
    }


}
