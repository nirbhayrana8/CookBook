package com.zenger.cookbook.repository

import android.app.Application
import android.database.MatrixCursor
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.api.classes.SuggestionObj
import com.zenger.cookbook.paging.DiscoverPagingSource
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.dao.SavedDao


class DataRepository(application: Application) {
    private val savedDao: SavedDao
    private val api by lazy { RecipeApi.getApi() }

    init {
        val database = RecipeDatabase.getInstance(application) as RecipeDatabase
        savedDao = database.savedDao()
    }

    fun randomRecipes() = Pager(
            config = PagingConfig(
                    pageSize = 4,
                    prefetchDistance = 1,
                    maxSize = 10,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = { DiscoverPagingSource() }
    )


    suspend fun getAutoCompleteSuggestions(query: String) = api.getAutoCompleteSuggestions(query = query)

    fun convertSuggestionToCursor(suggestions: List<SuggestionObj>): MatrixCursor {
        val cursor = MatrixCursor(arrayOf("_id", "title"))
        for (suggestion in suggestions) {
            cursor.addRow(arrayOf(suggestion.id, suggestion.title))
        }
        return cursor
    }

}

