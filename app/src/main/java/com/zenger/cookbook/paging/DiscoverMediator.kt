package com.zenger.cookbook.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.zenger.cookbook.api.RecipeApi
import com.zenger.cookbook.room.RecipeDatabase
import com.zenger.cookbook.room.tables.RemoteKeys
import com.zenger.cookbook.room.tables.SearchResultsTable
import retrofit2.HttpException
import java.io.InvalidObjectException

private const val STARTING_PAGE_INDEX = 1

@OptIn(ExperimentalPagingApi::class)
class DiscoverMediator(private val api: RecipeApi,
                       private val database: RecipeDatabase) : RemoteMediator<Int, SearchResultsTable>() {

    override suspend fun load(loadType: LoadType,
                              state: PagingState<Int, SearchResultsTable>): MediatorResult {

        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getAnchorPositionClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: STARTING_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                        ?: throw InvalidObjectException("Remote key should not be null for $loadType")

                if (remoteKeys.prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                remoteKeys.prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                if (remoteKeys?.nextKey == null) {
                    throw InvalidObjectException("Remote key should not be null for $loadType")
                }
                remoteKeys.nextKey
            }
        }

        try {

            val pageSize = state.config.pageSize
            val response = api.getRandomRecipe(number = pageSize)

            val items = response.recipes
            val endOfPaginationReached = items.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.apply {
                        remoteKeysDao().clearRemoteKeys()
                        searchDao().clearDB()
                    }
                }
                val prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1

                val keys = items.map {
                    RemoteKeys(repoId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                val results = items.map {
                    SearchResultsTable(itemId = it.id, imageUrl = it.imageUrl, title = it.title)
                }

                database.remoteKeysDao().insertAll(keys)
                database.searchDao().insertAll(results)
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            exception.printStackTrace()
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(
            state: PagingState<Int, SearchResultsTable>
    ): RemoteKeys? =

            state.lastItemOrNull()?.let { repo ->
                database.remoteKeysDao().remoteKeysRepoId(repo.itemId)
            }

    private suspend fun getRemoteKeyForFirstItem(
            state: PagingState<Int, SearchResultsTable>
    ): RemoteKeys? =
            state.firstItemOrNull()?.let { repo ->
                database.remoteKeysDao().remoteKeysRepoId(repo.itemId)
            }

    private suspend fun getAnchorPositionClosestToCurrentPosition(
            state: PagingState<Int, SearchResultsTable>
    ): RemoteKeys? =
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.itemId?.let { id ->
                    database.remoteKeysDao().remoteKeysRepoId(id)
                }
            }
}