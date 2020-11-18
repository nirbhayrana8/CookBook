package com.zenger.cookbook.api.classes

import com.google.gson.annotations.Expose

data class SuggestionObj(
        @Expose
        val id: Int,
        @Expose
        val title: String,
        val imageType: String
)