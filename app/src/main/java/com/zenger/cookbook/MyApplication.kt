package com.zenger.cookbook

import android.app.Application
import androidx.work.Configuration
import timber.log.Timber
import java.util.concurrent.Executors


class MyApplication : Application(), Configuration.Provider {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    override fun getWorkManagerConfiguration() =
            Configuration.Builder()
                    .setMinimumLoggingLevel(android.util.Log.INFO)
                    .setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()))
                    .build()
}