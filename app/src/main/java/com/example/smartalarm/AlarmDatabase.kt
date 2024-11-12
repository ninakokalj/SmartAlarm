package com.example.smartalarm
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AlarmEntity::class], version = 2)
@TypeConverters(Converters::class) // Register the converters
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao // Define the DAO

    companion object {
        @Volatile
        private var INSTANCE: AlarmDatabase? = null

        // Singleton to prevent multiple instances of the database being opened at the same time
        fun getDatabase(context: Context): AlarmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlarmDatabase::class.java,
                    "alarm_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}