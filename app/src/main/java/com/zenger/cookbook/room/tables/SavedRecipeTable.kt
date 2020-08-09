package com.zenger.cookbook.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Entity(tableName = "saved_recipes")
data class SavedRecipeTable(@PrimaryKey
                            @SerializedName("id")
                            @Expose
                            val id: Int,

                            @SerializedName("title")
                            @Expose
                            val title: String,

                            @SerializedName("image")
                            @Expose
                            @ColumnInfo(name = "image_url")
                            val imageUrl: String,

                            @SerializedName("sourceUrl")
                            @Expose
                            val sourceUrl: String?)