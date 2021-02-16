package com.zenger.cookbook.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.DeleteAccountSheetBinding
import com.zenger.cookbook.room.RecipeDatabase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import timber.log.Timber

class DeleteAccountDialog : BottomSheetDialogFragment() {

    lateinit var binding: DeleteAccountSheetBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

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
        Timber.d("Thread: ${Thread.currentThread().name}")
        val database = RecipeDatabase.getInstance(requireContext()) as RecipeDatabase
        val savedDAO = database.savedDao()

        val fireStore = Firebase.firestore
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val userDocRef = fireStore.collection("users").document(user.uid)

            userDocRef.collection("user_data")
                    .document("saved_recipes")
                    .get().addOnSuccessListener {
                        if (it.exists()) {
                            Timber.d("Chali paya oye")

                            Timber.d("Deleting saved recipes")

                            userDocRef.collection("user_data")
                                    .document("saved_recipes")
                                    .delete()
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Timber.d("Deleting firestore data")
                                            userDocRef.delete()
                                                    .addOnFailureListener { Timber.e(it) }
                                                    .addOnCompleteListener { deleteDataTask ->
                                                        if (deleteDataTask.isSuccessful) {
                                                            Timber.d("Deleting account")
                                                            user.delete()
                                                                    .addOnSuccessListener { Timber.d("Successfully deleted all user data") }
                                                                    .addOnFailureListener { exception ->
                                                                        if (exception is FirebaseAuthRecentLoginRequiredException) {
                                                                            AlertDialog.Builder(requireContext())
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
                                                                    }
                                                        }
                                                    }
                                        }
                                    }
                        }
                    }

        }

        savedDAO.clearDB()
    }
}