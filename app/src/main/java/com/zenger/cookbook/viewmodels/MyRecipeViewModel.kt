package com.zenger.cookbook.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.zenger.cookbook.repository.DataRepository
import com.zenger.cookbook.room.tables.RecipeTable
import kotlinx.coroutines.*

class MyRecipeViewModel(application: Application): AndroidViewModel(application) {

    private val viewModelJob: CompletableJob = Job()
    private val repository: DataRepository by lazy { DataRepository(application) }
    val recipes: LiveData<List<RecipeTable>> by lazy { repository.getRecipes() }
    var listState: Parcelable? = null

    init {
        CoroutineScope(Dispatchers.IO + viewModelJob).launch {
            recipes()
        }
    }

    private fun recipes(): LiveData<List<RecipeTable>> = recipes

    override fun onCleared() {
        super.onCleared()
        viewModelJob.complete()
    }
}