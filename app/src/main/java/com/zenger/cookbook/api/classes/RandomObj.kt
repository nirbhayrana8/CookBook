package com.zenger.cookbook.api.classes

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RandomObj(
        @Expose
        val id: Int,

        @Expose
        val title: String,

        @SerializedName("image")
        @Expose
        val imageUrl: String,

        @Expose
        val sourceUrl: String?
)