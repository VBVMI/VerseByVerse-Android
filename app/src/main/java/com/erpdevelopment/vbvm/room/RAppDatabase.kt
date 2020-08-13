package com.erpdevelopment.vbvm.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.versebyverseministry.models.Study

@Database(entities = arrayOf(RStudy::class), version = 1, exportSchema = false)
public abstract class RAppDatabase : RoomDatabase() {

    abstract fun studyDao(): RStudyDao


    private class RAppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)

            INSTANCE?.let { database ->
                scope.launch {
                    migrateOldData(database)
                }
            }
        }

        suspend fun migrateOldData(database: RAppDatabase) {
            val studyDao = database.studyDao()

            // Clean this up while we test things
            studyDao.deleteAll()

            // Let us fetch all the studies we have and migrate them?

            val oldStudies = Study.fetchAll()
            val newStudies = oldStudies.map { oldStudy ->
                return@map RStudy(
                        oldStudy.id,
                        oldStudy.bibleIndex,
                        oldStudy.title,
                        oldStudy.thumbnailSource,
                        oldStudy.podcastLink,
                        oldStudy.lessonCount,
                        oldStudy.description,
                        oldStudy.category,
                        oldStudy.url,
                        oldStudy.image160,
                        oldStudy.image300,
                        oldStudy.image600,
                        oldStudy.image900,
                        oldStudy.image1100,
                        oldStudy.image1400
                )
            }

            studyDao.mergeAPI(newStudies)
        }

    }

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: RAppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RAppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                context.deleteDatabase("app_room_database")

                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        RAppDatabase::class.java,
                "app_room_database"
                )
                        .addCallback(RAppDatabaseCallback(scope))
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}