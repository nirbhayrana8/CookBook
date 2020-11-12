package com.zenger.cookbook.api

import com.zenger.cookbook.room.tables.RecipeTable

data class RandomResponse (
        val recipes: List<RecipeTable>
)