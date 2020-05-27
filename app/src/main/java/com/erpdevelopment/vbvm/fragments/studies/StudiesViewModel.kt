package com.erpdevelopment.vbvm.fragments.studies

import android.util.Log
import androidx.lifecycle.*
import com.erpdevelopment.vbvm.api.APIManager
import com.jakewharton.rxrelay2.PublishRelay
import com.raizlabs.android.dbflow.rx2.language.RXSQLite
import com.raizlabs.android.dbflow.sql.language.SQLite
import com.raizlabs.android.dbflow.sql.queriable.ModelQueriable
import com.uber.autodispose.autoDispose
import com.uber.autodispose.android.lifecycle.autoDispose
import io.reactivex.Flowable
import io.reactivex.FlowableSubscriber
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Subscription
import org.versebyverseministry.models.Category
import org.versebyverseministry.models.Category_Table

sealed class StudiesViewEvent {}
class StudiesCategoriesDownloadFailed() : StudiesViewEvent()
class StudiesDownloadFailed() : StudiesViewEvent()
class CategoriesUpdated() : StudiesViewEvent()

class StudiesViewModel: ViewModel() {

    private val _categories: MutableList<Category> = mutableListOf()
    val categories: List<Category> = _categories
    private var categoriesDisposable: Flowable<Unit>? = null

    private val _hasDownloadedCategories = MutableLiveData<Boolean>()
    val hasDownloadedCategories: LiveData<Boolean> = _hasDownloadedCategories

    private val _hasDownloadedStudies = MutableLiveData<Boolean>()
    val hasDownloadedStudies: LiveData<Boolean> = _hasDownloadedStudies

    private val _viewEvents: PublishRelay<StudiesViewEvent> = PublishRelay.create()
    val viewEvents: Observable<StudiesViewEvent> = _viewEvents

    private val _loadingMediator = MediatorLiveData<StudiesLoad>()
    val isAllDataLoaded: LiveData<Boolean>

    inner class StudiesLoad internal constructor(
            var categoriesLoaded: Boolean = false,
            var studiesLoaded: Boolean = false
    ) {
        fun isComplete(): Boolean {
            return categoriesLoaded && studiesLoaded
        }
    }

    init {
        _loadingMediator.value = StudiesLoad()

        _loadingMediator.addSource(_hasDownloadedCategories) {
            val load = _loadingMediator.value
            load?.categoriesLoaded = true
            _loadingMediator.value = load
        }

        _loadingMediator.addSource(_hasDownloadedStudies) {
            val load = _loadingMediator.value
            load?.studiesLoaded = true
            _loadingMediator.value = load
        }

        val loadingMediator = MediatorLiveData<Boolean>()
        loadingMediator.addSource(_loadingMediator) {
            loadingMediator.value = !it.isComplete()
        }

        isAllDataLoaded = loadingMediator

        observeCategories()
        reloadData()
    }

    fun reloadData() {
        _hasDownloadedStudies.value = false
        _hasDownloadedCategories.value = false
        downloadCategories()
        downloadStudies()
    }

    private fun downloadCategories() {
        APIManager.getInstance().downloadCategories { success: Boolean ->
            if (success) {
                _hasDownloadedCategories.value = true
            } else {
                _viewEvents.accept(StudiesCategoriesDownloadFailed())
            }
        }
    }

    private fun downloadStudies() {
        APIManager.getInstance().downloadStudies { success: Boolean ->
            if (success) {
                _hasDownloadedStudies.value = true
            } else {
                _viewEvents.accept(StudiesDownloadFailed())
            }
        }
    }



    private fun observeCategories() {

//        categoriesDisposable?.dispose()
        categoriesDisposable = RXSQLite.rx(SQLite.select().from(Category::class.java).orderBy(Category_Table.order, true))
                .observeOnTableChanges().map {
                    it?.let {
                        Log.d(TAG, "😀 got a map")
                        _categories.clear()
                        _categories.addAll(it.queryList())
                        _viewEvents.accept(CategoriesUpdated())
                    }
                }
    }

    override fun onCleared() {
//        categoriesDisposable?.dispose()
        categoriesDisposable = null
        super.onCleared()
    }

    companion object {
        private const val TAG = "StudiesViewModel"
    }

}