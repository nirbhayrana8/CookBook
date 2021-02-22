package com.zenger.cookbook.ui.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentEmailBinding
import com.zenger.cookbook.repository.AuthRepository
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber

class EmailFragment : Fragment(R.layout.fragment_email) {

    private lateinit var binding: FragmentEmailBinding
    private val repo by lazy { AuthRepository(requireActivity().application) }

    private var validEmail = false
    private val disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_email, container, false)

        verifyEmailAddress()
        binding.button.setOnClickListener { saveEmailAddress() }

        return binding.root
    }

    private fun saveEmailAddress() {
        if (validEmail) {
            val inputMgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(binding.editText.windowToken, 0)
            binding.apply {
                editText.isEnabled = false
                button.isEnabled = false
                loading.visibility = View.VISIBLE
            }

            val user = FirebaseAuth.getInstance().currentUser
            val fireStore = Firebase.firestore
            user?.run {
                Timber.d("User is updating")
                updateEmail(binding.editText.text.toString())
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Timber.d("Email updated")
                                fireStore.collection("users").document(user.uid)
                                        .update("email", binding.editText.text.toString())
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                                findNavController().navigate(EmailFragmentDirections.actionEmailFragmentToNameFragment())
                                        }
                            } else {
                                if (it.exception?.message == "The email address is already in use by another account.") {
                                    binding.apply {
                                        editText.isEnabled = true
                                        button.isEnabled = true
                                        loading.visibility = View.GONE
                                    }
                                    AlertDialog.Builder(requireContext())
                                            .setMessage("This email address is already in use by another account. Please enter another email address.")
                                            .setPositiveButton("Ok") { dialog: DialogInterface, _: Int ->
                                                dialog.dismiss()
                                            }.show()
                                } else {
                                    it.exception?.run {
                                        Timber.e("well here it is: ${this.message}")
                                        throw this
                                    }

                                }

                            }
                        }

            }
        } else {
            val input = binding.editText.text.toString()
            if (input.isNotEmpty()) {
                if (!checkRegexMatch(input))
                    binding.editText.error = getString(R.string.enter_valid_email)
                else {
                    binding.editText.error = null
                    validEmail = true
                    saveEmailAddress()
                }
            } else {
                binding.editText.error = getString(R.string.enter_valid_email)
            }

        }
    }

    private fun verifyEmailAddress() {
        val observable = repo.verifyInput(binding.editText)

        observable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {
                disposable.add(d)
            }

            override fun onNext(t: String?) {
                val input = t ?: return
                if (!checkRegexMatch(input))
                    binding.editText.error = getString(R.string.enter_valid_email)
                else {
                    binding.editText.error = null
                    validEmail = true
                }
            }

            override fun onError(e: Throwable?) {}

            override fun onComplete() {}
        })
    }

    private fun checkRegexMatch(input: String): Boolean {
        val regex = Regex("^[a-zA-Z\\d]+?@[a-z]+?\\.[a-z]{2,3}\$")
        return input matches regex
    }

    override fun onPause() {
        super.onPause()
        disposable.dispose()
    }

}