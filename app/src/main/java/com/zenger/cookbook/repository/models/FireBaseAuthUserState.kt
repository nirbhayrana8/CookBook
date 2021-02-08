package com.zenger.cookbook.repository.models

import com.google.firebase.auth.FirebaseUser

sealed class FireBaseAuthUserState {

    data class UserSignedIn(val user: FirebaseUser) : FireBaseAuthUserState()

    object UserSignedOut : FireBaseAuthUserState()

    object UserUnknown : FireBaseAuthUserState()
}


