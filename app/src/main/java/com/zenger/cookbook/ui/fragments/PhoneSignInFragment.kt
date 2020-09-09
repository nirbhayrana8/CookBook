package com.zenger.cookbook.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentPhoneSignInBinding
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.TimeUnit

class PhoneSignInFragment : Fragment() {

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val viewModel: LoginViewModel by viewModels { factory }
    private lateinit var binding: FragmentPhoneSignInBinding
    private val disposables = CompositeDisposable()
    private var valid = false

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            viewModel.firebaseAuthWithCredentials(p0)
        }

        override fun onVerificationFailed(e: FirebaseException) {

            if (e is FirebaseAuthInvalidCredentialsException) {

                Snackbar.make(binding.container, "Invalid Phone Number", Snackbar.LENGTH_LONG).show()

            } else if (e is FirebaseTooManyRequestsException) {
                throw IllegalAccessException("SMS quota exceeded in firebase")
            }

            Timber.e(e, "Phone Verification Failed: ${e.message}")
            val view = binding.container
            val snackBar = Snackbar.make(view, "Sign In Failed", Snackbar.LENGTH_LONG)
            snackBar.setAction("Retry") {
                binding.loginPhone.performClick()
            }
            snackBar.show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {

            findNavController()
                    .navigate(PhoneSignInFragmentDirections.actionPhoneSignInFragmentToOtpFragment(p0))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback {
            findNavController()
                    .navigate(PhoneSignInFragmentDirections.actionPhoneSignInFragmentToLoginFragment())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_phone_sign_in, container, false)

        binding.backButton.setOnClickListener {
            findNavController().navigate(PhoneSignInFragmentDirections.actionPhoneSignInFragmentToLoginFragment())
        }

        binding.loginPhone.setOnClickListener {
            phoneAuth()
        }
        verifyPhone()
        return binding.root
    }

    private fun phoneAuth() {

        if (valid) {
            val code = "+${binding.countryCodeLayout.selectedCountryCode}"
            val phoneNumber = code + binding.phoneEditText.text.toString()

            PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, requireActivity(), callbacks)

            val inputMgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(binding.phoneEditText.windowToken, 0)

            binding.loading.visibility = View.VISIBLE
            binding.loginPhone.isEnabled = false
            binding.phoneEditText.isEnabled = false
            
        } else {
            binding.phoneEditText.error = getString(R.string.invalid_phone_number)
        }
    }


    private fun verifyPhone() {

        Timber.d("Function Called")

        val regex = Regex("^\\d{10}$")

        val observable = binding.phoneEditText.inputStream()
            .debounce(750, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<String>{

            override fun onSubscribe(d: Disposable?) {
                disposables.add(d)
            }

            override fun onNext(t: String?) {
                Timber.d("onNext")
                val input = t ?: return
                if (!(input matches regex)) {
                    binding.phoneEditText.error = getString(R.string.invalid_phone_number)
                } else {
                    binding.phoneEditText.error = null
                    valid = true
                }
            }

            override fun onError(e: Throwable?) {}

            override fun onComplete() {}
        })
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        disposables.clear()
    }

}