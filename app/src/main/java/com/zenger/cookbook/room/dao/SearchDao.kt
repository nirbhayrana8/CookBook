package com.zenger.cookbook.room.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.zenger.cookbook.room.tables.SearchResultsTable

@Dao
interface SearchDao : BaseDao<SearchResultsTable> {

    @Query("SELECT * FROM search_results")
    fun viewAll(): PagingSource<Int, SearchResultsTable>

    @Query("DELETE FROM search_results")
    suspend fun clearDB()
}