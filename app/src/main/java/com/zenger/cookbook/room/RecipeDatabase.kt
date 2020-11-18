package com.zenger.cookbook.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zenger.cookbook.room.dao.RecipeDao
import com.zenger.cookbook.room.dao.SavedDao
import com.zenger.cookbook.room.dao.SearchDao
import com.zenger.cookbook.room.tables.RecipeTable
import com.zenger.cookbook.room.tables.SavedRecipeTable
import com.zenger.cookbook.room.tables.SearchResultsTable

@Database(entities = [RecipeTable::class, SavedRecipeTable::class, SearchResultsTable::class], version = 3, exportSchema = false)
abstract class RecipeDatabase: RoomDatabase() {

    abstract fun recipeDao(): RecipeDao
    abstract fun savedDao(): SavedDao
    abstract fun searchDao(): SearchDao

    companion object {

        @Volatile
        private var INSTANCE: RecipeDatabase? = null

        fun getInstance(context: Context): RoomDatabase {

            synchronized(this) {

                var instance = INSTANCE
                if (instance == null) {

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe_database")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}