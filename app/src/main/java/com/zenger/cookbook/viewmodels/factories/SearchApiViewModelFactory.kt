package com.zenger.cookbook.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zenger.cookbook.viewmodels.SearchApiViewModel

@Suppress("UNCHECKED_CAST")
class SearchApiViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchApiViewModel::class.java)) {
            return SearchApiViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown Class")
        }
    }
}