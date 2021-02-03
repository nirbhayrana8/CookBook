package com.zenger.cookbook.viewmodels

import android.app.Application
import android.database.Cursor
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.zenger.cookbook.repository.DataRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel(application: Application) : ViewModel() {

    private val repository: DataRepository by lazy { DataRepository(application) }
    var listState: Parcelable? = null

    private val _cursor by lazy { MutableLiveData<Cursor>() }
    val cursor: LiveData<Cursor> get() = _cursor

    val randomRecipes = repository.randomRecipes().cachedIn(viewModelScope)

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