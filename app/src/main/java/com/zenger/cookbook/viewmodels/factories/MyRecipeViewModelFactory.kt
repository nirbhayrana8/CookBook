package com.zenger.cookbook.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zenger.cookbook.viewmodels.MyRecipeViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class MyRecipeViewModelFactory(private val application: Application): ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyRecipeViewModel::class.java)) {
            return MyRecipeViewModel(application) as T
        } else { throw IllegalArgumentException("Unknown Class") }
    }
}