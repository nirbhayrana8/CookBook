package com.zenger.cookbook.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.User

class SplashViewModel: ViewModel() {

    private val repository by lazy { AuthRepository() }
    lateinit var isUserAuthenticatedLiveData: LiveData<User>
    lateinit var userLiveData: LiveData<User>

    fun checkIfUserIsAuthenticated() {
        isUserAuthenticatedLiveData = repository.checkIfUserIsAuthenticatedInFirebase()
    }

    fun getUserFromDatabase(uid: String) {
        userLiveData = repository.searchUserInBackend(uid)
    }
}