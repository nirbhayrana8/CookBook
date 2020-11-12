package com.zenger.cookbook.viewmodels

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.zenger.cookbook.paging.DiscoverPagingSource
import com.zenger.cookbook.repository.DataRepository

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository by lazy { DataRepository(application) }
    var listState: Parcelable? = null

    val randomRecipes = Pager(
            config = PagingConfig(
                    pageSize = 16,
                    prefetchDistance = 6,
                    maxSize = 80,
                    enablePlaceholders = false
            ),
            pagingSourceFactory = { DiscoverPagingSource() }
    ).liveData.cachedIn(viewModelScope)

}