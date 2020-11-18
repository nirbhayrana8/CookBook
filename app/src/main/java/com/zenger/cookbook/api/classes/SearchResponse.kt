package com.zenger.cookbook.api.classes

import com.google.gson.annotations.Expose

data class SearchResponse(
        val offset: Int?,
        val number: Int?,

        @Expose
        val results: List<SearchObj>
)