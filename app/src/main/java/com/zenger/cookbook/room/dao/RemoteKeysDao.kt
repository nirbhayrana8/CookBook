package com.zenger.cookbook.room.dao

import androidx.room.Dao
import androidx.room.Query
import com.zenger.cookbook.room.tables.RemoteKeys

@Dao
interface RemoteKeysDao : BaseDao<RemoteKeys> {

    @Query("SELECT * FROM remote_keys WHERE repo_id = :repoId")
    suspend fun remoteKeysRepoId(repoId: Int): RemoteKeys?

    @Query("SELECT * FROM remote_keys ORDER BY id ASC")
    suspend fun viewAll(): List<RemoteKeys>

    @Query("DELETE FROM remote_keys")
    suspend fun clearRemoteKeys()

}