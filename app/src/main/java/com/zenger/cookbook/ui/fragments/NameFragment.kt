package com.zenger.cookbook.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentNameBinding
import timber.log.Timber

class NameFragment : Fragment(R.layout.fragment_name) {

    private lateinit var binding: FragmentNameBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_name, container, false)
        binding.editText.setAutofillHints(View.AUTOFILL_HINT_NAME)
        binding.button.setOnClickListener { setUserName() }
        return binding.root
    }

    private fun setUserName() {
        val name = binding.editText.text.toString()
        if (name.isNotEmpty()) {
            val inputMgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            binding.editText.isEnabled = false
            binding.button.isEnabled = false
            binding.loading.visibility = View.VISIBLE

            val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(binding.editText.text.toString())
                    .build()

            val user = FirebaseAuth.getInstance().currentUser
            val fireStore = Firebase.firestore

            user?.run {
                updateProfile(profileUpdates)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Timber.d("Name Added")
                                fireStore.collection("users").document(user.uid)
                                        .update("name", binding.editText.text.toString())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                                requireActivity().findNavController(R.id.main_nav_host_fragment).navigate(R.id.appFlowHostFragment)
                                        }
                            }
                        }


            }
        } else {
            binding.editText.error = getString(R.string.enter_valid_name)
        }

    }
}