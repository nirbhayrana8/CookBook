package com.zenger.cookbook.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.work.OneTimeWorkRequestBuilder
import com.google.firebase.auth.FirebaseAuth
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ActivityMainBinding
import com.zenger.cookbook.viewmodels.MainActivityViewModel
import com.zenger.cookbook.work.DataBaseCleanupWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                signUpOrLogin()
            } else {
                viewModel.setUser(user)
            }
        }
    }

    private fun signUpOrLogin() {
        //findNavController(binding.root.id).navigate(R.id.action_global_loginFragment)
    }

    private fun generateWorkRequest() =
            OneTimeWorkRequestBuilder<DataBaseCleanupWorker>()
                    .setInitialDelay(80, TimeUnit.SECONDS)
                    .addTag("DATABASE_CLEANUP")
                    .build()
}