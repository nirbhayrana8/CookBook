package com.zenger.cookbook.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.google.gson.JsonObject
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.dao.RecipeDao
import com.zenger.cookbook.room.dao.SavedDao
import com.zenger.cookbook.room.tables.RecipeTable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber


class DataRepository(application: Application) {
    private val dao: RecipeDao
    private val savedDao: SavedDao

    init {
        val database = RecipeDatabase.getInstance(application) as RecipeDatabase
        dao = database.recipeDao()
        savedDao = database.savedDao()
    }



    fun getRecipes(): LiveData<List<RecipeTable>> {
        Timber.d("getRecipes Called")
        return dao.viewAll()
    }

    fun getAnalysedRecipe(id: Int) {

        CoroutineScope(IO).launch {
            val result = async { RecipeApi.getApi().getAnalysedRecipe(id) }
            parseAnalysed(result.await())
        }

    }


    private fun parseAnalysed(result: JsonObject) {

        val json = result.asJsonArray
        var RESULT: String

        for (items in json) {

            val item = items as JsonObject
            val name = item.get("name").asString
        }
    }

    private suspend fun insertToDb(recipeTable: RecipeTable) {
        dao.insert(recipeTable)
        Timber.d("Insert Successful")
    }
}

