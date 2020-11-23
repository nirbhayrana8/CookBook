package com.zenger.cookbook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zenger.cookbook.room.dao.RemoteKeysDao
import com.zenger.cookbook.room.dao.SavedDao
import com.zenger.cookbook.room.dao.SearchDao
import com.zenger.cookbook.room.tables.RemoteKeys
import com.zenger.cookbook.room.tables.SavedRecipeTable
import com.zenger.cookbook.room.tables.SearchResultsTable

@Database(entities = [SavedRecipeTable::class, SearchResultsTable::class, RemoteKeys::class],
        version = 2, exportSchema = false)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun savedDao(): SavedDao
    abstract fun searchDao(): SearchDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {

        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RoomDatabase =
                INSTANCE ?: synchronized(this) {
                    buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe_database")
                        .fallbackToDestructiveMigration()
                        .build()
    }
}