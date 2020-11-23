package com.zenger.cookbook.room.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id")
        val id: Long = 0,

        @ColumnInfo(name = "repo_id")
        val repoId: Int,

        @ColumnInfo(name = "previous_key")
        val prevKey: Int?,

        @ColumnInfo(name = "next_key")
        val nextKey: Int?
)