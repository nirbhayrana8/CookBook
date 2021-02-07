package com.zenger.cookbook.api.models

import com.google.gson.annotations.Expose

data class SearchResponse(
        @Expose
        val results: List<BaseRecipe>
)