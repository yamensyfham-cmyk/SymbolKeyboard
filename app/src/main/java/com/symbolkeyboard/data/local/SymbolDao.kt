package com.symbolkeyboard.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.symbolkeyboard.data.model.Symbol
import kotlinx.coroutines.flow.Flow

@Dao
interface SymbolDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(symbols: List<Symbol>)

    @Query("SELECT * FROM symbols WHERE category = :category ORDER BY unicode ASC")
    fun getSymbolsByCategory(category: String): Flow<List<Symbol>>

    @Query("SELECT * FROM symbols WHERE name LIKE '%' || :query || '%' OR unicode LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchSymbols(query: String): Flow<List<Symbol>>

    @Query("SELECT * FROM symbols ORDER BY unicode ASC")
    fun getAllSymbols(): Flow<List<Symbol>>

    @Query("SELECT COUNT(*) FROM symbols")
    suspend fun getSymbolCount(): Int

    @Query("SELECT * FROM symbols WHERE unicode = :unicode")
    suspend fun getSymbolByUnicode(unicode: String): Symbol?

    @Query("DELETE FROM symbols")
    suspend fun clearAll()

    @Query("SELECT DISTINCT category FROM symbols ORDER BY category ASC")
    fun getCategories(): Flow<List<String>>
}
