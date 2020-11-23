package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zenger.cookbook.repository.DataRepository

class SearchApiViewModel(application: Application) : ViewModel() {

    private val repository: DataRepository by lazy { DataRepository(application) }

    fun searchRecipeByName(query: String) = repository.searchRecipeByName(query).cachedIn(viewModelScope)

}