package com.zenger.cookbook.viewmodels

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.zenger.cookbook.R

class LoginViewModel(application: Application) : ViewModel() {

    private var gso: GoogleSignInOptions =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(application.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
    private var mGoogleSignInClient: GoogleSignInClient

    private val _signInIntent = MutableLiveData<Intent>()
    val signInIntent: MutableLiveData<Intent> = _signInIntent

    init {
        mGoogleSignInClient = GoogleSignIn.getClient(application, gso)
    }

    fun signInWithGoogle() {
        signInIntent.value = mGoogleSignInClient.signInIntent
    }


    fun firebaseAuthWithGoogle(idToken: String) {

    }
}