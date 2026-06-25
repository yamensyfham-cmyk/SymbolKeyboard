package com.symbolkeyboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symbolkeyboard.data.model.Symbol
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {

    @Query("""
        SELECT s.* FROM symbols s 
        INNER JOIN recents r ON s.unicode = r.symbolUnicode 
        ORDER BY r.usedAt DESC LIMIT :limit
    """)
    fun getRecents(limit: Int = 30): Flow<List<Symbol>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addRecent(recent: RecentEntity)

    @Query("DELETE FROM recents")
    suspend fun clearRecents()
}
