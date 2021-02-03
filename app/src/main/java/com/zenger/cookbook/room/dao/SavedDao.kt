package com.zenger.cookbook.room.dao


import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.zenger.cookbook.room.tables.SavedRecipeTable

@Dao
interface SavedDao: BaseDao<SavedRecipeTable> {

    @Query("SELECT * FROM saved_recipes")
    fun viewAll(): PagingSource<Int, SavedRecipeTable>

    @Query("DELETE FROM saved_recipes")
    suspend fun clearDB()

    @Query("DELETE FROM saved_recipes WHERE item_id = :recipeId")
    suspend fun delete(recipeId: Int)
}