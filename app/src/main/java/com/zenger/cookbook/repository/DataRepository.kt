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

    fun getRandomRecipes() {

        CoroutineScope(IO).launch {
            val result = RecipeApi.getApi().getRandomRecipe()
            Timber.d("SomeWhere")
            parseRandom(result)
        }
    }

    fun getRecipes(): LiveData<List<RecipeTable>> {
        Timber.d("getRecipes Called")
        return dao.viewAll()
    }

    private suspend fun parseRandom(json: JsonObject) {

        val recipes = json.getAsJsonArray("recipes")

        Timber.d(recipes.size().toString())
        for (item in recipes) {

            val recipe = item as JsonObject
            val id = recipe.get("id").asInt
            val title = recipe.get("title").asString
            val image = recipe.get("image").asString
            val sourceUrl: String? = recipe.get("sourceUrl").asString
            val result = RecipeTable(id, title, image, sourceUrl)
            Timber.d(result.toString())
            insertToDb(result)
        }
    }

    private suspend fun insertToDb(recipeTable: RecipeTable) {
        dao.insert(recipeTable)
        Timber.d("Insert Successful")
    }
}

