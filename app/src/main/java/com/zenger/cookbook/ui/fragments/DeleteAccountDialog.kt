package com.zenger.cookbook.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.DeleteAccountSheetBinding
import com.zenger.cookbook.room.RecipeDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class DeleteAccountDialog : BottomSheetDialogFragment() {

    lateinit var binding: DeleteAccountSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.delete_account_sheet, container, false)

        binding.positiveButton.setOnClickListener {
            lifecycleScope.launch(IO) {
                deleteAccount()
            }
            dismiss()
        }
        binding.negativeButton.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    private suspend fun deleteAccount() {

        val database = RecipeDatabase.getInstance(requireContext()) as RecipeDatabase
        val savedDAO = database.savedDao()

        val firestore = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        val userDocRef = firestore.collection("users").document(user?.uid.toString())

        val task1 = userDocRef.collection("user_data").document("saved_recipes")
                .get().await()
        if (task1.exists()) {
            userDocRef.collection("user_data").document("saved_recipes")
                    .delete()
                    .addOnSuccessListener { Timber.d("Successfully deleted saved recipes") }
                    .addOnFailureListener { Timber.e(it, "Failed to delete data") }
        }


        /* userDocRef.collection("user_data").document("saved_recipes")
                 .delete()
                 .addOnSuccessListener {
                     userDocRef.delete()
                             .addOnSuccessListener {
                                 user?.delete()
                                         ?.addOnFailureListener { exception ->
                                             if (exception is FirebaseAuthRecentLoginRequiredException) {
                                                 AlertDialog.Builder(context)
                                                         .setMessage(R.string.reauthenticate_message)
                                                         .setCancelable(false)
                                                         .setPositiveButton(R.string.com_facebook_loginview_log_in_button) { _: DialogInterface, _: Int ->
                                                             findNavController().navigate(R.id.action_global_login_flow_nav)
                                                         }
                                                         .create()
                                                         .show()
                                             } else {
                                                 Timber.e(exception, "Failed to delete data")
                                             }
                                         }?.addOnSuccessListener {
                                             Timber.d("Successfully deleted all user data")
                                         }
                             }.addOnFailureListener {
                                 Timber.e(it, "Failed to delete data")
                             }
                 }.addOnFailureListener {
                     Timber.e(it, "Failed to delete data")
                 }
 */
        savedDAO.clearDB()
    }
}