package com.zenger.cookbook.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class DetailViewModel(url: String): ViewModel() {

    val webViewUrl by lazy { MutableLiveData<String>().apply { value = url } }

}