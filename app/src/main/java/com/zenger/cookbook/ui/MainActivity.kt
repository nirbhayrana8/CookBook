package com.zenger.cookbook.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.work.OneTimeWorkRequestBuilder
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ActivityMainBinding
import com.zenger.cookbook.repository.models.FireBaseAuthUserState
import com.zenger.cookbook.viewmodels.MainActivityViewModel
import com.zenger.cookbook.viewmodels.factories.MainActivityViewModelFactory
import com.zenger.cookbook.work.DataBaseCleanupWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val factory by lazy { MainActivityViewModelFactory(application) }
    private val viewModel: MainActivityViewModel by viewModels { factory }

    private val userStateObserver = Observer<FireBaseAuthUserState> { userState ->
        when (userState) {
            is FireBaseAuthUserState.UserUnknown -> {

            }

            is FireBaseAuthUserState.UserSignedIn -> {
                Timber.d("User Signed IN \n UID: ${userState.user.uid}")
                viewModel.searchUserInBackend(userState.user.uid)
                viewModel.userLiveData.observe(this) {
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                    navHostFragment?.navController?.navigate(R.id.appFlowHostFragment)
                }
            }

            is FireBaseAuthUserState.UserSignedOut -> {
                Timber.d("User NOT Signed IN")
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.main_nav_host_fragment) as NavHostFragment?
                navHostFragment?.navController?.navigate(R.id.action_global_login_flow_nav)
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
    }

    override fun onStop() {
        super.onStop()
        viewModel.authStateLiveData.removeObserver(userStateObserver)
    }

    private fun generateWorkRequest() =
            OneTimeWorkRequestBuilder<DataBaseCleanupWorker>()
                    .setInitialDelay(80, TimeUnit.SECONDS)
                    .addTag("DATABASE_CLEANUP")
                    .build()
}