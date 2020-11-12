package com.zenger.cookbook.paging

import androidx.paging.PagingSource
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.room.tables.RecipeTable
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
class DiscoverPagingSource: PagingSource<Int, RecipeTable>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecipeTable> {
        val position = params.key ?: STARTING_PAGE_INDEX

        return try {
            val randomRequest = RecipeApi.getApi().getRandomRecipe(number = params.loadSize)
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