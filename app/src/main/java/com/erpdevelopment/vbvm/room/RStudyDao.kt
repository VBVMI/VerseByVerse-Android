package com.erpdevelopment.vbvm.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RStudyDao {

    @Query("SELECT * FROM study_table ORDER BY bibleIndex ASC")
    fun getBibleOrderedStudies(): LiveData<List<RStudy>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(study: RStudy)

    @Query("DELETE FROM study_table")
    suspend fun deleteAll()
}