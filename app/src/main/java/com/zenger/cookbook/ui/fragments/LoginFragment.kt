package com.zenger.cookbook.ui.fragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentLoginBinding
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import timber.log.Timber

private const val RC_SIGN_IN = 9687

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var callbackManager: CallbackManager

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val loginViewModel: LoginViewModel by viewModels { factory }

    private val mainNavController by lazy { activity?.findNavController(R.id.main_nav_host_fragment) }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        callbackManager = CallbackManager.Factory.create()

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)

        binding.apply {
            viewModel = loginViewModel

            oLoginFacebook.setPermissions("email", "public_profile")
            oLoginFacebook.fragment = this@LoginFragment
            fragment = this@LoginFragment

            oLoginFacebook.registerCallback(callbackManager, object :
                    FacebookCallback<LoginResult> {

                override fun onSuccess(result: LoginResult?) {
                    val credentials = FacebookAuthProvider.getCredential(result?.accessToken!!.token)
                    loginViewModel.firebaseAuthWithCredentials(credentials)
                    setUpUserAccount()
                }

                override fun onCancel() {
                    showSnackBarOnError("Facebook")
                }

                override fun onError(error: FacebookException?) {
                    throw FacebookException("${error?.message}")
                }

            })
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.loginPhone.setOnClickListener {
            findNavController().navigate(R.id.phoneSignInFragment)
        }

        loginViewModel.signInIntent.observe(viewLifecycleOwner, {
            startActivityForResult(it, RC_SIGN_IN)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                val authCredentials = GoogleAuthProvider.getCredential(account?.idToken!!, null)
                loginViewModel.firebaseAuthWithCredentials(authCredentials)
                setUpUserAccount()

            } catch (e: Exception) {
                Timber.d("Google Sign In Failed")
                showSnackBarOnError("Google")
            }

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    fun onClickFacebookLogin() {
        binding.oLoginFacebook.performClick()
    }

    fun otherLoginClick() {
        ObjectAnimator.ofFloat(binding.loginGoogle,
                "translationY", -500f).apply {

            duration = 500
            start()
        }

        animateButtons(binding.loginFacebook, true)
        animateButtons(binding.loginPhone, true)
        animateButtons(binding.otherLogin, false)
    }


    private fun animateButtons(view: View, boolean: Boolean) {

        view.apply {
            isEnabled = boolean

            animate()
                    .alpha(if (boolean) 1f else 0f)
                    .duration = 580
        }

    }

    private fun setUpUserAccount() {
        loginViewModel.authenticatedUserData.observe(viewLifecycleOwner, { user ->

            if (user.isNew) {
                Timber.d("New User")
                loginViewModel.createNewUser(user)
                loginViewModel.createdUserLiveData.observe(viewLifecycleOwner) {

                    if (it.isCreated) {
                        val view = requireActivity().findViewById<View>(R.id.container)
                        val snackBar = Snackbar.make(view, "User Created", Snackbar.LENGTH_LONG)
                        snackBar.show()
                    }
                    goToMainAppFlow()
                }
            } else {
                Timber.d("Old User")
                goToMainAppFlow()
            }
        })
    }

    private fun showSnackBarOnError(OAuthProvider: String) {

        val view = requireActivity().findViewById<View>(R.id.container)
        val snackBar = Snackbar.make(view, "$OAuthProvider Sign In Failed", Snackbar.LENGTH_LONG)
                .setAction("Retry") {

                    if (OAuthProvider == "Google") {
                        startActivityForResult(loginViewModel.signInIntent.value, RC_SIGN_IN)
                    } else {
                        onClickFacebookLogin()
                    }

                }
                .setActionTextColor(Color.RED)
        snackBar.show()

    }

    private fun goToMainAppFlow() {
        mainNavController?.navigate(R.id.appFlowHostFragment)
    }
}