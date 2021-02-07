package com.zenger.cookbook.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseUser
import com.zenger.cookbook.repository.User

class MainActivityViewModel: ViewModel() {

    private val _currentUser by lazy { MutableLiveData<User>() }
    val currentUser: LiveData<User> get() = _currentUser

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
}