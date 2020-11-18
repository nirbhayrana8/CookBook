package com.zenger.cookbook.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.zenger.cookbook.api.classes.RandomResponse
import com.zenger.cookbook.api.classes.SearchResponse
import com.zenger.cookbook.api.classes.SuggestionObj
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.spoonacular.com/recipes/"
private const val API_KEY = "1f38adb099804c48814af282525512cd"

interface RecipeApi {

    @GET("random")
    suspend fun getRandomRecipe(@Query("apiKey") key: String = API_KEY,
                                @Query("number") number: Int): RandomResponse

    @GET("{id}/analyzedInstructions?apiKey=$API_KEY")
    suspend fun getAnalysedRecipe(@Path("id") id: Int): JsonObject

    @GET("autocomplete")
    suspend fun getAutoCompleteSuggestions(@Query("apiKey") key: String = API_KEY,
                                           @Query("number") number: Int = 4,
                                           @Query("query") query: String): List<SuggestionObj>

    suspend fun searchRecipe(@Query("apiKey") key: String = API_KEY,
                             @Query("number") number: Int = 100,
                             @Query("query") query: String): SearchResponse

    companion object {

        private val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .followRedirects(true)
                .followSslRedirects(true)
                .build()

        fun getApi(): RecipeApi = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory
                        .create(
                                GsonBuilder().excludeFieldsWithoutExposeAnnotation().create())
                )
            .build()
            .create(RecipeApi::class.java)
    }
}