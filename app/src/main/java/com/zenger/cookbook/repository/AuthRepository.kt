package com.zenger.cookbook.repository

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore

    private val user by lazy { User() }

    fun firebaseAuthWithCredentials(authCredentials: AuthCredential): MutableLiveData<User> {
        val authenticatedUserMutableLiveData = MutableLiveData<User>()
        firebaseAuth.signInWithCredential(authCredentials)
                .addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        val isNewUser = authTask.result?.additionalUserInfo?.isNewUser!!
                        val firebaseUser = firebaseAuth.currentUser

                        if (firebaseUser != null) {
                            val uid = firebaseUser.uid
                            val name = firebaseUser.displayName
                            val email = firebaseUser.email
                            val photo = firebaseUser.photoUrl
                            val phoneNumber = firebaseUser.phoneNumber

                            Timber.d("Name: $name, Email: $email, UID: $uid")
                            val user = User(uid, name, email, photoUrl = photo.toString(), phoneNumber = phoneNumber)
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
                "email" to authenticatedUser.email,
                "photoUrl" to authenticatedUser.photoUrl,
                "phoneNumber" to authenticatedUser.phoneNumber
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
            } else {
                newUserMutableLiveData.value = authenticatedUser
            }
        }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed To Create user")
                }

        return newUserMutableLiveData
    }

    fun checkIfUserIsAuthenticatedInFirebase(): MutableLiveData<User> {
        val isUserAuthenticatedInFirebase = MutableLiveData<User>()
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser == null) {
            user.isAuthenticated = false
            isUserAuthenticatedInFirebase.value = user
        } else {
            user.uid = firebaseUser.uid
            user.isAuthenticated = true
            isUserAuthenticatedInFirebase.value = user
        }
        return isUserAuthenticatedInFirebase
    }

    fun searchUserInBackend(uid: String): MutableLiveData<User> {
        val userLiveData = MutableLiveData<User>()
        val uidRef = database.collection("users").document(uid)

        uidRef.get().addOnSuccessListener {
            if (it.exists()) {
                val user = it.toObject(User::class.java)
                userLiveData.value = user
            } else {
                Timber.d("User Not found")
                throw NullPointerException("User not found in backend")
            }
        }
        return userLiveData
    }

    fun verifyInput(textInputEditText: TextInputEditText) =

            textInputEditText.inputStream()
                    .debounce(750, TimeUnit.MILLISECONDS)
                    .filter { it.isNotEmpty() }
                    .distinctUntilChanged()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())


    private fun TextInputEditText.inputStream(): Observable<String> =
            Observable.create {
                addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        it.onNext(s.toString())
                    }

                })
            }

}