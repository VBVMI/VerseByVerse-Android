package com.erpdevelopment.vbvm.room

import androidx.lifecycle.LiveData

class RStudyRepository(private val studyDao: RStudyDao) {

    val bibleOrderedStudies: LiveData<List<RStudy>> = studyDao.getBibleOrderedStudies()

    suspend fun insert(study: RStudy) {
        studyDao.insert(study)
    }

}