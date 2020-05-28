package com.erpdevelopment.vbvm.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(RStudy::class), version = 1, exportSchema = false)
public abstract class RAppDatabase : RoomDatabase() {

    abstract fun studyDao(): RStudyDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RAppDatabase? = null

        fun getDatabase(context: Context): RAppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RAppDatabase::class.java,
                "app_room_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}