package com.zenger.cookbook.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.tables.SavedRecipeTable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RecipeWorker(private val context: Context,
                   workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    private val database by lazy { RecipeDatabase.getInstance(context) as RecipeDatabase }
    private val savedDao by lazy { database.savedDao() }

    override suspend fun doWork(): Result {
        var result: Result? = null
        return withContext(Dispatchers.IO) {

            val firebaseAuth = FirebaseAuth.getInstance()
            val database = Firebase.firestore

            val operationType = inputData.getString("operation_type")

            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val uid = firebaseUser.uid
                val docRef = database.collection("users").document(uid)
                        .collection("user_data").document("saved_recipes")

                result = when (operationType) {

                    "ADD" -> performAddOrUpdateOperation(docRef)

                    "DELETE" -> performDeleteOperation(docRef)

                    else -> Result.failure()
                }
            }
            return@withContext result!!
        }
    }

    private suspend fun performAddOrUpdateOperation(docRef: DocumentReference): Result {
        val recipeId = inputData.getInt("RecipeId", 0)
        val imageUrl = inputData.getString("ImageUrl") ?: ""
        val title = inputData.getString("Title") ?: ""

        val recipe = SavedRecipeTable(itemId = recipeId, imageUrl = imageUrl, title = title)
        savedDao.insert(recipe)

        val data = hashMapOf("recipes" to recipeId)

        var result: Result? = null

        docRef.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        docRef.update("recipes", FieldValue.arrayUnion(recipeId))
                                .addOnSuccessListener {
                                    result = Result.success()
                                }.addOnFailureListener {
                                    result = Result.failure()
                                }
                    } else {
                        docRef.set(data)
                                .addOnSuccessListener {
                                    result = Result.success()
                                }.addOnFailureListener {
                                    result = Result.failure()
                                }
                    }
                }.addOnFailureListener { result = Result.failure() }
        return result!!
    }

    private suspend fun performDeleteOperation(docRef: DocumentReference): Result {
        val recipeId = inputData.getInt("RecipeId", 0)
        savedDao.delete(recipeId)

        var result: Result? = null

        docRef.get()
                .addOnSuccessListener {
                    if (it.exists()) {
                        docRef.update("recipes", FieldValue.arrayRemove(recipeId))
                                .addOnSuccessListener {
                                    result = Result.success()
                                }.addOnFailureListener {
                                    result = Result.failure()
                                }
                    }
                }.addOnFailureListener { result = Result.failure() }
        return result!!
    }
}