package com.zenger.cookbook.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthCredential
import com.zenger.cookbook.R
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.repository.User

class LoginViewModel(application: Application) : ViewModel() {

    private val authRepository by lazy { AuthRepository() }

    lateinit var authenticatedUserData: LiveData<User>
    lateinit var createdUserLiveData: LiveData<User>

    private val _signInIntent = MutableLiveData<Intent>()
    val signInIntent: MutableLiveData<Intent> = _signInIntent


    // Google sign in components
    private var gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    private var mGoogleSignInClient: GoogleSignInClient

    init { mGoogleSignInClient = GoogleSignIn.getClient(application, gso) }

    fun signInWithGoogle() {
        signInIntent.value = mGoogleSignInClient.signInIntent
    }

    fun firebaseAuthWithCredentials(authCredential: AuthCredential) {
        authenticatedUserData = authRepository.firebaseAuthWithCredentials(authCredential)
    }


    fun createNewUser(authenticatedUser: User) {
        createdUserLiveData = authRepository.createUserInFireStoreIfNotExists(authenticatedUser)
    }



}