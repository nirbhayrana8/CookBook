package com.zenger.cookbook.viewmodels.factories

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zenger.cookbook.viewmodels.SplashViewModel

@Suppress("UNCHECKED_CAST")
class SplashViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(application) as T
        } else {
            throw IllegalArgumentException("Unknown Class")
        }
    }
}