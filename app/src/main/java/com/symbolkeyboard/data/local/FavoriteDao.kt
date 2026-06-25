package com.symbolkeyboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT f.symbolUnicode FROM favorites f ORDER BY f.addedAt DESC")
    fun getFavoriteUnicodes(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE symbolUnicode = :unicode")
    suspend fun removeFavorite(unicode: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE symbolUnicode = :unicode)")
    suspend fun isFavorite(unicode: String): Boolean

    @Query("SELECT symbolUnicode FROM favorites")
    suspend fun getAllFavoriteUnicodes(): List<String>
}
