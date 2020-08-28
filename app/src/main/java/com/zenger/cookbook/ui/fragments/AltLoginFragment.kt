package com.zenger.cookbook.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentAltLoginBinding
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import timber.log.Timber
import java.lang.Exception

class AltLoginFragment : Fragment() {

    private lateinit var binding: FragmentAltLoginBinding
    private lateinit var callbackManager: CallbackManager

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val viewModel: LoginViewModel by viewModels { factory }

    private val mainNavController by lazy { activity?.findNavController(R.id.main_nav_host_fragment) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        callbackManager = CallbackManager.Factory.create()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_alt_login, container, false)

        binding.oLoginFacebook.setPermissions("email", "public_profile")
        binding.oLoginFacebook.fragment = this

        binding.fragment = this

        binding.oLoginFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult?) {
                viewModel.handleFacebookAccessToken(result?.accessToken!!)
                viewModel.authenticatedUserData.observe(viewLifecycleOwner, Observer { user ->

                    if (user.isNew) {
                        Timber.d("New User")
                        viewModel.createNewUser(user)
                        viewModel.createdUserLiveData.observe(viewLifecycleOwner, Observer {

                            if (it.isCreated) {
                                val view = requireActivity().findViewById<View>(R.id.container)
                                val snackBar = Snackbar.make(view, "User Created", Snackbar.LENGTH_LONG)
                                snackBar.show()
                            }
                            goToMainAppFlow()
                        })
                    } else {
                        Timber.d("Old User")
                        goToMainAppFlow()
                    }

                })
            }

            override fun onCancel() {
                val view = requireActivity().findViewById<View>(R.id.container)
                val snackBar = Snackbar.make(view, "Facebook Sign In Failed", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        onClickFacebookLogin()
                    }
                    .setActionTextColor(Color.RED)
                snackBar.show()
            }

            override fun onError(error: FacebookException?) {
                throw Exception("Facebook Exception: ${error?.message}")
            }

        })

        return binding.root
    }


    fun onClickFacebookLogin() {
        binding.oLoginFacebook.performClick()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun goToMainAppFlow() {
        mainNavController?.navigate(LoginHostFragmentDirections.actionLoginHostFragmentToAppFlowHostFragment())
    }
}