package com.zenger.cookbook.repository.models

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@MainThread
fun FirebaseAuth.newFireBaseAuthStateLiveData(
        context: CoroutineContext = EmptyCoroutineContext
): LiveData<FireBaseAuthUserState> {

    val ld = FireBaseAuthStateLiveData(this)
    return liveData(context) {
        emitSource(ld)
    }
}

class FireBaseAuthStateLiveData(private val auth: FirebaseAuth) : LiveData<FireBaseAuthUserState>() {
    private val authStateListener = MyAuthStateListener()

    init {
        value = FireBaseAuthUserState.UserUnknown
    }

    override fun onActive() {
        auth.addAuthStateListener(authStateListener)
    }

    override fun onInactive() {
        auth.removeAuthStateListener(authStateListener)
    }

    private inner class MyAuthStateListener : FirebaseAuth.AuthStateListener {
        override fun onAuthStateChanged(auth: FirebaseAuth) {
            val user = auth.currentUser
            value = if (user != null) FireBaseAuthUserState.UserSignedIn(user) else FireBaseAuthUserState.UserSignedOut
        }

    }
}
