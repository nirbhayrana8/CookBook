package com.zenger.cookbook.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.zenger.cookbook.room.tables.RecipeTable
import com.zenger.cookbook.room.tables.SavedRecipeTable

@Dao
interface SavedDao: BaseDao<SavedRecipeTable> {

    @JvmSuppressWildcards
    @Query("SELECT * FROM saved_recipes")
    fun viewAll(): LiveData<List<RecipeTable>>

    @JvmSuppressWildcards
    @Query("DELETE FROM saved_recipes")
    suspend fun clear()
}