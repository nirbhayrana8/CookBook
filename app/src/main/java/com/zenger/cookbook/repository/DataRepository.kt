package com.zenger.cookbook.repository

import android.app.Application
import android.database.MatrixCursor
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.api.classes.SuggestionObj
import com.zenger.cookbook.paging.DiscoverPagingSource
import com.zenger.cookbook.paging.SearchMediator
import com.zenger.cookbook.room.RecipeDatabase


class DataRepository(application: Application) {

    private val database by lazy { RecipeDatabase.getInstance(application) as RecipeDatabase }
    private val api by lazy { RecipeApi.getApi() }


    fun randomRecipes() = Pager(
            config = PagingConfig(
                    pageSize = 4,
                    prefetchDistance = 1,
                    maxSize = 10,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = { DiscoverPagingSource(api) }
    ).liveData

    suspend fun getAutoCompleteSuggestions(query: String) = api.getAutoCompleteSuggestions(query = query)

    fun convertSuggestionToCursor(suggestions: List<SuggestionObj>): MatrixCursor {
        val cursor = MatrixCursor(arrayOf("_id", "title"))
        suggestions.map { suggestion ->
            cursor.addRow(arrayOf(suggestion.id, suggestion.title))
        }
        return cursor
    }

    fun searchRecipeByName(query: String) = Pager(
            config = PagingConfig(
                    pageSize = 4,
                    prefetchDistance = 1,
                    maxSize = 10,
                    enablePlaceholders = false
            ),
            remoteMediator = SearchMediator(
                    api = api,
                    database = database,
                    query = query
            ),
            pagingSourceFactory = { database.searchDao().viewAll() }
    ).liveData

}

