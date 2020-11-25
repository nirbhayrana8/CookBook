package com.zenger.cookbook.api.models

import com.google.gson.annotations.Expose

data class RecipeInstruction(
        @Expose val name: String?,
        @Expose val steps: List<RecipeStep>

) {
    data class RecipeStep(
            @Expose val number: Int,
            @Expose val step: String,
            @Expose val ingredients: List<RecipeIngredients>?
    ) {
        data class RecipeIngredients(
                @Expose val name: String
        )
    }

}