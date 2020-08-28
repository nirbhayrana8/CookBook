package com.zenger.cookbook.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zenger.cookbook.viewmodels.DetailViewModel

class DetailViewModelFactory(private val url: String): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(url) as T
        }
        else
            throw IllegalArgumentException("Unknown Class")
    }
}