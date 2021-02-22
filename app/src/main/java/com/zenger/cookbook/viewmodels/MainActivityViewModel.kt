package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.repository.models.User
import timber.log.Timber

class MainActivityViewModel(private val application: Application) : ViewModel() {

    private val repository by lazy { AuthRepository(application) }

    val authStateLiveData: LiveData<FireBaseAuthUserState> get() = getAuthStateLiveData(FirebaseAuth.getInstance())

    private val _signInComplete by lazy { MutableLiveData(true) }
    val signInComplete: LiveData<Boolean> get() = _signInComplete

    private val _userLiveData by lazy { MutableLiveData<User>() }
    val userLiveData: LiveData<User> get() = _userLiveData

    private fun getAuthStateLiveData(auth: FirebaseAuth): LiveData<FireBaseAuthUserState> {
        return repository.getAuthStateLiveData(auth, viewModelScope.coroutineContext)
    }

    fun setSignInStatus(complete: Boolean) {
        Timber.d("Function to set sign in status called")
        _signInComplete.value = complete
    }

    fun searchUserInBackend(uid: String) {
        _userLiveData.value = repository.searchUserInBackend(uid)
    }

}