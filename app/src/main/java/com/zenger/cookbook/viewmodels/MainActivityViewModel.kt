package com.zenger.cookbook.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.repository.models.User

class MainActivityViewModel(application: Application) : ViewModel() {

    private val repository by lazy { AuthRepository(application) }
    private val _currentUser by lazy { MutableLiveData<User>() }
    val currentUser: LiveData<User> get() = _currentUser

    val authStateLiveData: LiveData<FireBaseAuthUserState> get() = getAuthStateLiveData(FirebaseAuth.getInstance())

    lateinit var userLiveData: LiveData<User>

    fun setUser(firebaseUser: FirebaseUser, isNew: Boolean? = null) {
        val user = User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName,
                email = firebaseUser.email,
                phoneNumber = firebaseUser.phoneNumber,
                photoUrl = firebaseUser.photoUrl?.toString(),
        )

        isNew?.let {
            user.isNew = isNew
        }

        _currentUser.value = user
    }

    private fun getAuthStateLiveData(auth: FirebaseAuth): LiveData<FireBaseAuthUserState> {
        return repository.getAuthStateLiveData(auth, viewModelScope.coroutineContext)
    }

    fun searchUserInBackend(uid: String) {
        userLiveData = repository.searchUserInBackend(uid)
    }
}