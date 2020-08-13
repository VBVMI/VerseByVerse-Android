package com.erpdevelopment.vbvm.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RStudyDao : RBaseDao<RStudy> {

    @Query("SELECT * FROM study_table ORDER BY bibleIndex ASC")
    fun getBibleOrderedStudies(): LiveData<List<RStudy>>

    @Query("SELECT * FROM study_table")
    suspend fun selectAll(): List<RStudy>

    @Query("DELETE FROM study_table")
    suspend fun deleteAll()

    @Query("DELETE FROM study_table where id IN (:ids)")
    suspend fun deleteById(vararg ids: String) : Int

    @Transaction
    suspend fun mergeAPI(studies: List<RStudy>) {
        val storedStudies = selectAll()
        val idsToDelete: MutableSet<String> = mutableSetOf()
        storedStudies.forEach { idsToDelete.add(it.id) }

        val studiesToUpdate: MutableList<RStudy> = mutableListOf()
        val studiesToInsert: MutableList<RStudy> = mutableListOf()

        studies.forEach { apiStudy ->
            if (idsToDelete.remove(apiStudy.id)) {
                studiesToUpdate.add(apiStudy)
            } else {
                studiesToInsert.add(apiStudy)
            }
        }

        if (idsToDelete.size > 0) {
            deleteById(*idsToDelete.toTypedArray())
        }
        if (studiesToUpdate.size > 0) {
            update(*studiesToUpdate.toTypedArray())
        }
        if (studiesToInsert.size > 0) {
            insert(*studiesToInsert.toTypedArray())
        }
    }
}