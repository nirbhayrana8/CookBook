package com.zenger.cookbook.api.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SearchObj(
        @Expose
        val id: Int,

        val calories: Int?,
        val carbs: String?,
        val fat: String?,

        @Expose
        @SerializedName("image")
        val imageUrl: String,

        val imageType: String?,
        val protein: String?,

        @Expose
        val title: String,
)