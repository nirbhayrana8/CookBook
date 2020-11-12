package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zenger.cookbook.repository.DataRepository


class DetailViewModel(id: Int, imageUrl: String, application: Application): ViewModel() {

    private val repository: DataRepository by lazy { DataRepository(application) }
    private val _steps = MutableLiveData<String>()
    val steps: LiveData<String> = _steps
    val imageUrl by lazy { MutableLiveData<String>().apply { value = imageUrl } }

    init {
        repository.getAnalysedRecipe(id)
    }

    fun updateSteps(string: String) {
        _steps.value = string
    }
}