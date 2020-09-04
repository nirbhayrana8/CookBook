package com.zenger.cookbook.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.PhoneAuthProvider
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentOtpBinding
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class OtpFragment : Fragment() {

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val viewModel: LoginViewModel by viewModels { factory }
    private lateinit var binding: FragmentOtpBinding
    private val verificationCode: String by lazy { OtpFragmentArgs.fromBundle(requireArguments()).verificationCode }
    private var validOtp = false
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        verifyOtp()

        binding.backButton.setOnClickListener {
            findNavController()
                    .navigate(OtpFragmentDirections.actionOtpFragmentToPhoneSignInFragment())
        }

        binding.loginPhone.setOnClickListener {
            verifyPhoneWithCode()
        }

        return binding.root
    }

    private fun verifyPhoneWithCode() {
        if (validOtp) {

            val inputMgr = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMgr.hideSoftInputFromWindow(binding.phoneEditText.windowToken, 0)

            val credential = PhoneAuthProvider.getCredential(verificationCode, binding.phoneEditText.text.toString())
            viewModel.firebaseAuthWithCredentials(credential)

            binding.loading.visibility = View.VISIBLE
            binding.loginPhone.isEnabled = false
        }
    }

    private fun verifyOtp() {

        val regex = Regex("^\\d{6}$")
        val observable = binding.phoneEditText.inputStream()
                .debounce(750, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

        observable.subscribe(object : Observer<String>{
            override fun onSubscribe(d: Disposable?) {
                disposables.add(d)
            }

            override fun onNext(t: String?) {
                val input = t ?: return
                if (!(input matches regex)) {
                    binding.phoneEditText.error = getString(R.string.invalid_phone_number)
                } else {
                    binding.phoneEditText.error = null
                    validOtp = true
                }
            }

            override fun onError(e: Throwable?) {}

            override fun onComplete() {}
        })

    }

    private fun TextInputEditText.inputStream(): Observable<String> =
            Observable.create {
                addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                            s: CharSequence?, start: Int,
                            count: Int, after: Int
                    ) {
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                    override fun afterTextChanged(s: Editable?) {
                        it.onNext(s.toString())
                    }

                })
            }

}