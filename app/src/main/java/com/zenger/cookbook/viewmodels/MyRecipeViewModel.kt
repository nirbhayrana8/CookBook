package com.zenger.cookbook.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.zenger.cookbook.repository.DataRepository
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job

class MyRecipeViewModel(application: Application) : ViewModel() {

    private val viewModelJob: CompletableJob = Job()
    private val repository: DataRepository by lazy { DataRepository(application) }

    //    val recipes: LiveData<List<RecipeTable>> by lazy { repository.getRecipes() }
    var listState: Parcelable? = null

    /*init {
        CoroutineScope(Dispatchers.IO + viewModelJob).launch {
            recipes()
        }
    }

    private fun recipes(): LiveData<List<RecipeTable>> = recipes*/

    override fun onCleared() {
        super.onCleared()
        viewModelJob.complete()
    }
}