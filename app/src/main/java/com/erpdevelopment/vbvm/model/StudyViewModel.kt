package com.erpdevelopment.vbvm.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.erpdevelopment.vbvm.room.RAppDatabase
import com.erpdevelopment.vbvm.room.RStudy
import com.erpdevelopment.vbvm.room.RStudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudyViewModel(application: Application): AndroidViewModel(application) {

    private val repository: RStudyRepository

    val bibleOrderedStudies: LiveData<List<RStudy>>

    init {
        val studyDao = RAppDatabase.getDatabase(application).studyDao()
        repository = RStudyRepository(studyDao)
        bibleOrderedStudies = repository.bibleOrderedStudies
    }

    fun insertStudy(study: RStudy) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(study)
    }

}