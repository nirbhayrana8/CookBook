package com.zenger.cookbook.repository

import android.app.Application
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.withTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.repository.models.User
import com.zenger.cookbook.repository.models.newFireBaseAuthStateLiveData
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.tables.SavedRecipeTable
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class AuthRepository(application: Application) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val deviceDatabase by lazy { RecipeDatabase.getInstance(application) as RecipeDatabase }

    fun getAuthStateLiveData(auth: FirebaseAuth, context: CoroutineContext): LiveData<FireBaseAuthUserState> {
        return auth.newFireBaseAuthStateLiveData(context)
    }

    fun firebaseAuthWithCredentials(authCredentials: AuthCredential): LiveData<User> {
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
                            val user = User(uid = uid,
                                    name = name,
                                    email = email,
                                    photoUrl = photo.toString(),
                                    phoneNumber = phoneNumber,
                                    isNew = isNewUser)

                            authenticatedUserMutableLiveData.value = user
                        }
                    } else {
                        Timber.d("Error: ${authTask.exception?.message}")
                    }
                }
        return authenticatedUserMutableLiveData
    }

    fun createUserInFireStore(authenticatedUser: User): LiveData<User> {
        val newUserMutableLiveData = MutableLiveData<User>()

        val user = hashMapOf(
                "name" to authenticatedUser.name,
                "email" to authenticatedUser.email,
                "photoUrl" to authenticatedUser.photoUrl,
                "phoneNumber" to authenticatedUser.phoneNumber
        )

        val uidRef = database.collection("users").document(authenticatedUser.uid)
        uidRef.set(user)
                .addOnSuccessListener {
                    authenticatedUser.isCreated = true
                    newUserMutableLiveData.value = authenticatedUser
                    Timber.d("Created new user in firestore \n user: $user")
                }
                .addOnFailureListener { exception ->
                    Timber.e(exception, "Failed To Create user")
                }

        return newUserMutableLiveData
    }

    fun searchUserInBackend(uid: String): MutableLiveData<User> {
        val userLiveData = MutableLiveData<User>()
        val uidRef = database.collection("users").document(uid)

        uidRef.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        val user = it.toObject<User>()
                        userLiveData.value = user
                    } else {
                        Timber.d("User Not found")
                        throw IllegalAccessException("User not found in backend")
                    }
                }
                .addOnFailureListener {
                    Timber.d("Failure in background search")
                }
        return userLiveData
    }

    @Suppress("UNCHECKED_CAST")
    suspend fun getUserData() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val uid = user.uid
            val uidRef = database.collection("users").document(uid)
                    .collection("user_data").document("saved_recipes")

            val docSnap: DocumentSnapshot = try {
                uidRef.get().await()
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }

            if (docSnap.exists()) {
                val data = docSnap.data
                if (data != null) {
                    val recipes: ArrayList<Int> = data["recipes"] as ArrayList<Int>
                    saveUserDataOnDevice(recipes)
                }
            }
        }
    }

    private suspend fun saveUserDataOnDevice(recipes: ArrayList<Int>) {

        recipes.forEach { recipeId ->

            val recipe = try {
                RecipeApi.getApi().getRecipeInformation(recipeId)
            } catch (e: Exception) {
                Timber.e(e, "Exception Thrown")
                throw e
            }
            val savedRecipe = SavedRecipeTable(
                    itemId = recipe.id,
                    imageUrl = recipe.imageUrl,
                    title = recipe.title)
            Timber.d(savedRecipe.toString())

            deviceDatabase.withTransaction {
                deviceDatabase.savedDao().insert(savedRecipe)
            }
        }
    }

    fun verifyInput(textInputEditText: TextInputEditText): Observable<String> =

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