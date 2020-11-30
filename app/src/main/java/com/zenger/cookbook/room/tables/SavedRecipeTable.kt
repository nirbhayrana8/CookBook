package com.zenger.cookbook.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_recipes")
data class SavedRecipeTable(@PrimaryKey(autoGenerate = true)
                            val id: Long = 0,

                            val item_id: Int,

                            val title: String,

                            @ColumnInfo(name = "image_url")
                            val imageUrl: String)