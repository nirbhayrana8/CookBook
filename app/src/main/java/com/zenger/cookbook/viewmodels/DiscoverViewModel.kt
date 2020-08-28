package com.zenger.cookbook.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.zenger.cookbook.repository.DataRepository
import com.zenger.cookbook.room.tables.RecipeTable
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

class DiscoverViewModel(application: Application): AndroidViewModel(application) {
    private val viewModelJob: CompletableJob = Job()
    private val repository: DataRepository by lazy { DataRepository(application) }
    val recipes: LiveData<List<RecipeTable>> by lazy { repository.getRecipes() }
    var listState: Parcelable? = null

    init {

        CoroutineScope(IO + viewModelJob).launch {
             repository.getRandomRecipes()
           recipes()
            Timber.d(" Recipes: ${recipes.value.toString()}")
            }
    }

    private fun recipes(): LiveData<List<RecipeTable>> = recipes

    override fun onCleared() {
        super.onCleared()
        viewModelJob.complete()
    }
}