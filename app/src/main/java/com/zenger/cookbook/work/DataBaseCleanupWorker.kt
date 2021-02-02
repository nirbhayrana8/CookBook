package com.zenger.cookbook.work

import android.content.Context
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zenger.cookbook.room.RecipeDatabase

class DataBaseCleanupWorker(private val context: Context,
                            workerParameters: WorkerParameters) : CoroutineWorker(context, workerParameters) {

    override suspend fun doWork(): Result {

        val database = RecipeDatabase.getInstance(context) as RecipeDatabase

        return database.withTransaction {
            database.searchDao().clearDB()
            Result.success()
        }
    }
}