package com.example.smartalarm

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val id = intent?.getIntExtra("ALARM_ID", 0) ?: 0
        val label = intent?.getStringExtra("ALARM_LABEL") ?: "Alarm"
        val sound = intent?.getIntExtra("ALARM_SOUND", R.raw.lofi) ?: R.raw.lofi
        val mission = intent?.getStringExtra("ALARM_MISSION") ?: "None"
        val repeat = intent?.getBooleanExtra("REPEAT", false) == true

        Log.d("AlarmReceiver", "Received alarm with ID: $id")

        // Show a notification
        context?.let { ctx ->
            val notificationManager =
                ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channelId = "ALARM_CHANNEL"

            // Create the notification channel
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Alarm Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for Alarm Notifications"
                }
                notificationManager.createNotificationChannel(channel)
            }

            // Start the alarm sound
            SoundManager.startSound(ctx, sound)

            // Intent, ki bo odprl app
            val activityIntent = Intent(ctx, MainActivity::class.java).apply {
                putExtra("ALARM_MISSION", mission)
                putExtra("ALARM_ID", id)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            val pendingIntent = PendingIntent.getActivity(
                ctx,
                id, // Unique request code for each alarm
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            // Create the notification
            val notification = NotificationCompat.Builder(ctx, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("alarm is ringing!")
                .setContentText(label)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            // Show the notification
            notificationManager.notify(1, notification)

            if (repeat) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.WEEK_OF_YEAR, 1) // next week
                    set(Calendar.DAY_OF_WEEK, get(Calendar.DAY_OF_WEEK))
                    set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, get(Calendar.MINUTE))
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra("ALARM_LABEL", label)
                    putExtra("ALARM_SOUND", sound)
                    putExtra("ALARM_MISSION", mission)
                    putExtra("REPEAT", true)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                    Log.d("AlarmReceiver", "Rescheduled alarm for bla at ${calendar.time}")
                } catch (e: SecurityException) {
                    Log.e("AlarmReceiver", "Failed to schedule exact alarm. ${e.message}")
                }

            }
        }
    }
}