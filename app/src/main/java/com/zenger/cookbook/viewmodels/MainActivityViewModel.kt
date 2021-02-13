package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.repository.models.User

class MainActivityViewModel(application: Application) : ViewModel() {

    private val repository by lazy { AuthRepository(application) }

    val authStateLiveData: LiveData<FireBaseAuthUserState> get() = getAuthStateLiveData(FirebaseAuth.getInstance())

    lateinit var userLiveData: LiveData<User>

    private fun getAuthStateLiveData(auth: FirebaseAuth): LiveData<FireBaseAuthUserState> {
        return repository.getAuthStateLiveData(auth, viewModelScope.coroutineContext)
    }

    fun searchUserInBackend(uid: String) {
        userLiveData = repository.searchUserInBackend(uid)
    }
}