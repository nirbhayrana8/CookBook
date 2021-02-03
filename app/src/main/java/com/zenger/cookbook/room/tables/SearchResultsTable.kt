package com.zenger.cookbook.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "search_results")
data class SearchResultsTable(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,

        @SerializedName("id")
        @ColumnInfo(name = "item_id")
        val itemId: Int,

        @ColumnInfo(name = "image_url")
        @SerializedName("image")
        val imageUrl: String,

        @SerializedName("title")
        val title: String
)