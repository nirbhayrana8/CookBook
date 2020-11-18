package com.zenger.cookbook.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.zenger.cookbook.room.tables.RecipeTable

@Dao
interface RecipeDao: BaseDao<RecipeTable> {

    @JvmSuppressWildcards
    @Query("SELECT * FROM recipe_table")
    fun viewAll(): LiveData<List<RecipeTable>>

    @JvmSuppressWildcards
    @Query("DELETE FROM recipe_table")
    suspend fun clearDB()

}