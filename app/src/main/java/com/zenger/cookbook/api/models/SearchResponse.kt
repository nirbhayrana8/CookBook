package com.zenger.cookbook.api.models

import com.google.gson.annotations.Expose

data class SearchResponse(
        val offset: Int?,
        val number: Int?,

        @Expose
        val results: List<BaseRecipe>
)