package com.zenger.cookbook.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.PhoneAuthProvider
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentOtpBinding
import com.zenger.cookbook.repository.AuthRepository
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber


class OtpFragment : Fragment() {

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val viewModel: LoginViewModel by viewModels { factory }

    private lateinit var binding: FragmentOtpBinding
    private val verificationCode: String by lazy { OtpFragmentArgs.fromBundle(requireArguments()).verificationCode }
    private var validOtp = false

    private val repo by lazy { AuthRepository() }
    private val disposables = CompositeDisposable()

    private val mainNavController by lazy { activity?.findNavController(R.id.main_nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.onBackPressedDispatcher?.addCallback {
            findNavController().navigate(R.id.phoneSignInFragment)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp, container, false)
        verifyOtp()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.phoneSignInFragment)
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

            binding.loading.visibility = View.VISIBLE
            binding.loginPhone.isEnabled = false
            binding.phoneEditText.isEnabled = false

            val credential = PhoneAuthProvider.getCredential(verificationCode, binding.phoneEditText.text.toString())
            viewModel.firebaseAuthWithCredentials(credential)

            viewModel.authenticatedUserData.observe(viewLifecycleOwner, { user ->

                if (user.isNew) {
                    Timber.d("New User")
                    viewModel.createNewUser(user)
                    viewModel.createdUserLiveData.observe(viewLifecycleOwner, {

                        if (it.isCreated) {
                            Snackbar.make(binding.container , "User Created", Snackbar.LENGTH_LONG).show()
                        }
                        goToMainAppFlow()
                    })
                } else {
                    Timber.d("Old User")
                    goToMainAppFlow()
                }
            })

        }
    }

    private fun goToMainAppFlow() {
        mainNavController?.navigate(R.id.appFlowHostFragment)
    }

    private fun verifyOtp() {

        val regex = Regex("^\\d{6}$")
        val observable = repo.verifyInput(binding.phoneEditText)

        observable.subscribe(object : Observer<String> {
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


    override fun onDestroyView() {
        super.onDestroyView()
        disposables.dispose()
    }

}