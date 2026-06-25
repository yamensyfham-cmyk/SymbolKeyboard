package com.symbolkeyboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symbolkeyboard.data.model.Symbol
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("""
        SELECT s.* FROM symbols s 
        INNER JOIN favorites f ON s.unicode = f.symbolUnicode 
        ORDER BY f.addedAt DESC
    """)
    fun getFavorites(): Flow<List<Symbol>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE symbolUnicode = :unicode")
    suspend fun removeFavorite(unicode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE symbolUnicode = :unicode)")
    suspend fun isFavorite(unicode: String): Boolean

    @Query("SELECT symbolUnicode FROM favorites")
    suspend fun getAllFavoriteUnicodes(): List<String>
}
