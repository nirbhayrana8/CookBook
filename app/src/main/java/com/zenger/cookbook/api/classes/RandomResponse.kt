package com.zenger.cookbook.api.classes

import com.google.gson.annotations.Expose

data class RandomResponse(
        @Expose
        val recipes: List<RandomObj>
)