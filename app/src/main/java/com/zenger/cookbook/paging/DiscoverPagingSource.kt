package com.zenger.cookbook.paging

import androidx.paging.PagingSource
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.api.classes.RandomObj
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class DiscoverPagingSource(private val api: RecipeApi) : PagingSource<Int, RandomObj>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RandomObj> {
        val position = params.key ?: STARTING_PAGE_INDEX

        return try {
            val randomRequest = api.getRandomRecipe(number = params.loadSize)
            val recipes = randomRequest.recipes

            LoadResult.Page(
                    data = recipes,
                    prevKey = if (position == STARTING_PAGE_INDEX) null else position - 1,
                    nextKey = if (recipes.isEmpty()) null else position + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}