package com.zenger.cookbook.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.work.OneTimeWorkRequestBuilder
import com.zenger.cookbook.R
import com.zenger.cookbook.databinding.ActivityMainBinding
import com.zenger.cookbook.work.DataBaseCleanupWorker
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

    }

    private fun generateWorkRequest() =
            OneTimeWorkRequestBuilder<DataBaseCleanupWorker>()
                    .setInitialDelay(80, TimeUnit.SECONDS)
                    .addTag("DATABASE_CLEANUP")
                    .build()
}