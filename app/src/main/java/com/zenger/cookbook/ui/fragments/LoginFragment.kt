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
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.FragmentLoginBinding
import com.zenger.cookbook.viewmodels.LoginViewModel
import com.zenger.cookbook.viewmodels.factories.LoginViewModelFactory
import timber.log.Timber

private const val RC_SIGN_IN = 9687

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding

    private val factory by lazy { LoginViewModelFactory(requireActivity().application) }
    private val viewModel: LoginViewModel by viewModels { factory }

    private val mainNavController by lazy { activity?.findNavController(R.id.main_nav_host_fragment) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.fragment = this

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.signInIntent.observe(viewLifecycleOwner, Observer {
            startActivityForResult(it, RC_SIGN_IN)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account?.idToken!!)

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

            } catch (e: Exception) {
                Timber.d("Google Sign In Failed")

                val view = requireActivity().findViewById<View>(R.id.container)
                val snackBar = Snackbar.make(view, "Google Sign In Failed", Snackbar.LENGTH_LONG)
                    .setAction("Retry") {
                        startActivityForResult(viewModel.signInIntent.value, RC_SIGN_IN)
                    }
                    .setActionTextColor(Color.RED)
                snackBar.show()
            }

        }
    }

    fun onClick() {
        Timber.d("CLICKERRRR")
        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToAltLoginFragment())
    }

    private fun goToMainAppFlow() {
        mainNavController?.navigate(LoginHostFragmentDirections.actionLoginHostFragmentToAppFlowHostFragment())
    }
}