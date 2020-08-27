package com.zenger.cookbook.repository

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore

    fun firebaseSignInWithGoogle(authCredentials: AuthCredential): MutableLiveData<User> {
        val authenticatedUserMutableLiveData = MutableLiveData<User>()
        firebaseAuth.signInWithCredential(authCredentials)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val isNewUser = authTask.result?.additionalUserInfo?.isNewUser!!
                    val firebaseUser = firebaseAuth.currentUser

                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        val name = firebaseUser.displayName!!
                        val email = firebaseUser.email!!

                        Timber.d("Name: $name, Email: $email, UID: $uid")
                        val user = User(uid, name, email)
                        user.isNew = isNewUser

                        authenticatedUserMutableLiveData.value = user
                    }
                } else {
                    Timber.d("Error: ${authTask.exception?.message}")
                }
            }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFireStoreIfNotExists(authenticatedUser: User): MutableLiveData<User> {
        val newUserMutableLiveData = MutableLiveData<User>()

        val user = hashMapOf(
            "name" to authenticatedUser.name,
            "email" to authenticatedUser.email
        )

        val uidRef = database.collection("users").document(authenticatedUser.uid)

        uidRef.get().addOnSuccessListener {

               if (!it.exists()) {
                   uidRef.set(user)
                       .addOnSuccessListener {
                           authenticatedUser.isCreated = true
                           newUserMutableLiveData.value = authenticatedUser
                       }
                       .addOnFailureListener { exception ->
                           Timber.e(exception, "Failed To Create user")
                       }
               } else { newUserMutableLiveData.value = authenticatedUser }
            }
            .addOnFailureListener { exception ->
                Timber.e(exception, "Failed To Create user")
        }

        return newUserMutableLiveData
    }
}