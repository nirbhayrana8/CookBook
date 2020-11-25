package com.zenger.cookbook.api.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recipe(
        val id: Int,
        val title: String,
        val imageUrl: String
) : Parcelable