package com.symbolkeyboard.data.repository

import com.symbolkeyboard.data.model.Symbol
import kotlinx.coroutines.flow.Flow

interface SymbolRepository {
    suspend fun init()
    fun getPage(pageNumber: Int): List<Symbol>
    fun getTotalPages(): Int
    fun searchSymbols(query: String): List<Symbol>
    fun getSymbolsByCategory(category: String): List<Symbol>
    fun getSymbolByUnicode(unicode: String): Symbol?
    fun getFavorites(): Flow<List<Symbol>>
    fun getRecents(limit: Int): Flow<List<Symbol>>
    suspend fun toggleFavorite(unicode: String)
    suspend fun isFavorite(unicode: String): Boolean
    suspend fun addRecent(unicode: String)
    suspend fun clearRecents()
    fun clearCache()
}
