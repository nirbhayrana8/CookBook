package com.zenger.cookbook.room.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @JvmSuppressWildcards
    suspend fun insert (obj: T)

    @Insert
    @JvmSuppressWildcards
    suspend fun insert (vararg obj: T)

    @Delete
    @JvmSuppressWildcards
    suspend fun delete (obj: T)

}