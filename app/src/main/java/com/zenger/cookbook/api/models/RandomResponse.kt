package com.zenger.cookbook.api.models

import com.google.gson.annotations.Expose

data class RandomResponse(
        @Expose
        val recipes: List<RandomObj>
)