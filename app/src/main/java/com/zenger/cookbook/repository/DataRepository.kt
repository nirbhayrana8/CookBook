package com.zenger.cookbook.repository

import android.app.Application
import android.database.MatrixCursor
import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.api.SuggestionObj
import com.zenger.cookbook.paging.DiscoverPagingSource
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.dao.RecipeDao
import com.zenger.cookbook.room.dao.SavedDao
import com.zenger.cookbook.room.tables.RecipeTable
import timber.log.Timber


class DataRepository(application: Application) {
    private val dao: RecipeDao
    private val savedDao: SavedDao
    private val api by lazy { RecipeApi.getApi() }

    init {
        val database = RecipeDatabase.getInstance(application) as RecipeDatabase
        dao = database.recipeDao()
        savedDao = database.savedDao()
    }

    fun randomRecipes() = Pager(
            config = PagingConfig(
                    pageSize = 4,
                    prefetchDistance = 1,
                    maxSize = 80,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = { DiscoverPagingSource() }
    )

    fun getRecipes(): LiveData<List<RecipeTable>> {
        Timber.d("getRecipes Called")
        return dao.viewAll()
    }


    suspend fun getAutoCompleteSuggestions(query: String) = api.getAutoCompleteSuggestions(query = query)

    fun convertSuggestionToCursor(suggestions: List<SuggestionObj>): MatrixCursor {
        val cursor = MatrixCursor(arrayOf("_id", "title"))
        for (suggestion in suggestions) {
            cursor.addRow(arrayOf(suggestion.id, suggestion.title))
        }
        return cursor
    }


    private suspend fun insertToDb(recipeTable: RecipeTable) {
        dao.insert(recipeTable)
        Timber.d("Insert Successful")
    }
}

