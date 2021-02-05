package com.zenger.cookbook.api.models

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BaseRecipe(
        @Expose
        val id: Int,

        @Expose
        val title: String,

        @SerializedName("image")
        @Expose
        val imageUrl: String
) : Parcelable