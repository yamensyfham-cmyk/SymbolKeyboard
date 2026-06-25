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
        INNER JOIN favorites f ON s.unicode = f.symbol_unicode 
        ORDER BY f.added_at DESC
    """)
    fun getFavorites(): Flow<List<Symbol>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE symbol_unicode = :unicode")
    suspend fun removeFavorite(unicode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE symbol_unicode = :unicode)")
    suspend fun isFavorite(unicode: String): Boolean

    @Query("SELECT symbol_unicode FROM favorites")
    suspend fun getAllFavoriteUnicodes(): List<String>
}
