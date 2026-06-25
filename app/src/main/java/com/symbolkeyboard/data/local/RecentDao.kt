package com.symbolkeyboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {

    @Query("SELECT r.symbolUnicode FROM recents r ORDER BY r.usedAt DESC LIMIT :limit")
    fun getRecentUnicodes(limit: Int = 30): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(recent: RecentEntity)

    @Query("DELETE FROM recents")
    suspend fun clearRecents()
}
