package com.zenger.cookbook.viewmodels

import android.app.Application
import android.database.Cursor
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.zenger.cookbook.repository.DataRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DataRepository by lazy { DataRepository(application) }
    var listState: Parcelable? = null

    private val _cursor by lazy { MutableLiveData<Cursor>() }
    val cursor: LiveData<Cursor> get() = _cursor

    val randomRecipes = repository.randomRecipes().liveData.cachedIn(viewModelScope)

    fun getSuggestions(query: String) {
        viewModelScope.launch(IO) {
            try {
                val response = repository.getAutoCompleteSuggestions(query)
                withContext(Main) {
                    _cursor.value = repository.convertSuggestionToCursor(response)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


}