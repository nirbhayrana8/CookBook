package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.User

class SplashViewModel(application: Application) : ViewModel() {

    private val repository by lazy { AuthRepository(application) }
    lateinit var isUserAuthenticatedLiveData: LiveData<User>
    lateinit var userLiveData: LiveData<User>

    fun checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = repository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun getUserFromDatabase(uid: String) {
        userLiveData = repository.searchUserInBackend(uid)
    }
}