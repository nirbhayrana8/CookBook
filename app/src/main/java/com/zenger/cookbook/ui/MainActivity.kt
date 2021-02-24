package com.zenger.cookbook.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.work.OneTimeWorkRequestBuilder
import com.google.android.material.snackbar.Snackbar
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ActivityMainBinding
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.repository.models.NetworkState
import com.zenger.cookbook.viewmodels.MainActivityViewModel
import com.zenger.cookbook.viewmodels.factories.MainActivityViewModelFactory
import com.zenger.cookbook.work.DataBaseCleanupWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val factory by lazy { MainActivityViewModelFactory(application) }
    private val viewModel: MainActivityViewModel by viewModels { factory }

    private var initialRun = true

    companion object {
        var signInComplete = true
    }

    private val userStateObserver by lazy {
        Observer<FireBaseAuthUserState> { userState ->
            when (userState) {
                is FireBaseAuthUserState.UserUnknown -> {
                }

                is FireBaseAuthUserState.UserSignedIn -> {
                    Timber.d("User Signed IN \n UID: ${userState.user.uid}")

                    viewModel.setSignInStatus(signInComplete)

                    viewModel.signInComplete.observe(this) { complete ->
                        Timber.d("Sign in status value: $complete")
                        if (complete) {
                            viewModel.searchUserInBackend(userState.user.uid)
                            viewModel.userLiveData.observe(this) {
                                if (it != null) {
                                    findNavController(R.id.main_nav_host_fragment).navigate(R.id.appFlowHostFragment)
                                } else
                                    Timber.d("User Null")
                            }
                        }
                    }

                }

                is FireBaseAuthUserState.UserSignedOut -> {
                    Timber.d("User NOT Signed IN")
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                    navHostFragment?.navController?.navigate(R.id.action_global_login_flow_nav)
                }
            }
        }
    }

    private val networkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                generateSnackBar(NetworkState.CONNECTED)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                generateSnackBar(NetworkState.NO_CONNECTION)
            }

            override fun onUnavailable() {
                super.onUnavailable()
                generateSnackBar(NetworkState.UNAVAILABLE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        viewModel.authStateLiveData.observeForever(userStateObserver)

        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.authStateLiveData.removeObserver(userStateObserver)

        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    private fun generateWorkRequest() =
            OneTimeWorkRequestBuilder<DataBaseCleanupWorker>()
                    .setInitialDelay(80, TimeUnit.SECONDS)
                    .addTag("DATABASE_CLEANUP")
                    .build()

    private fun generateSnackBar(state: NetworkState) {
        val root = binding.coordinatorLayout

        val message: String? = when (state) {
            NetworkState.NO_CONNECTION -> "No internet connection found. Please check device connection"
            NetworkState.UNAVAILABLE -> "Internet connection is unavailable."
            NetworkState.CONNECTED -> if (initialRun) null else "Connected to the internet"
        }
        message?.run {
            initialRun = false
            Snackbar.make(root, message, Snackbar.LENGTH_LONG)
                    .show()
        }
    }
}