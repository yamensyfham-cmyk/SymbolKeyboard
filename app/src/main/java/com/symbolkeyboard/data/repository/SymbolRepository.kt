package com.symbolkeyboard.data.repository

import com.symbolkeyboard.data.model.Symbol
import kotlinx.coroutines.flow.Flow

interface SymbolRepository {
    fun getSymbolsByCategory(category: String): Flow<List<Symbol>>
    fun searchSymbols(query: String): Flow<List<Symbol>>
    fun getAllSymbols(): Flow<List<Symbol>>
    fun getFavorites(): Flow<List<Symbol>>
    fun getRecents(limit: Int): Flow<List<Symbol>>
    suspend fun toggleFavorite(unicode: String)
    suspend fun isFavorite(unicode: String): Boolean
    suspend fun addRecent(unicode: String)
    suspend fun getSymbolCount(): Int
    suspend fun loadSymbolsFromAssets()
    suspend fun clearRecents()
}
