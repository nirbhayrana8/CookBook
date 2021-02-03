package com.zenger.cookbook.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zenger.cookbook.repository.DataRepository

class MyRecipeViewModel(application: Application) : ViewModel() {

    private val repository: DataRepository by lazy { DataRepository(application) }
    var listState: Parcelable? = null

    val savedRecipes = repository.viewSavedRecipes().cachedIn(viewModelScope)
}