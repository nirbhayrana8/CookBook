package com.zenger.cookbook.paging

import androidx.paging.PagingSource
import com.zenger.cookbook.api.classes.SearchObj

private const val STARTING_PAGE_INDEX = 1

class SearchApiPagingSource : PagingSource<Int, SearchObj>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchObj> {
        val position = params.key ?: STARTING_PAGE_INDEX
        TODO("Not yet implemented")
    }
}