package com.zenger.cookbook.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "search_results")
data class SearchResultsTable(
        @PrimaryKey
        @SerializedName("id")
        val id: Int,

        @ColumnInfo(name = "image_url")
        @SerializedName("image")
        val imageUrl: String,

        @SerializedName("title")
        val title: String,
)