package com.symbolkeyboard.data.repository

import com.symbolkeyboard.data.local.FavoriteDao
import com.symbolkeyboard.data.local.FavoriteEntity
import com.symbolkeyboard.data.local.RecentDao
import com.symbolkeyboard.data.local.RecentEntity
import com.symbolkeyboard.data.local.SymbolDao
import com.symbolkeyboard.data.model.Symbol
import com.symbolkeyboard.util.SymbolParser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolRepositoryImpl @Inject constructor(
    private val symbolDao: SymbolDao,
    private val favoriteDao: FavoriteDao,
    private val recentDao: RecentDao,
    private val symbolParser: SymbolParser
) : SymbolRepository {

    override fun getSymbolsByCategory(category: String): Flow<List<Symbol>> {
        return symbolDao.getSymbolsByCategory(category)
    }

    override fun searchSymbols(query: String): Flow<List<Symbol>> {
        return symbolDao.searchSymbols(query)
    }

    override fun getAllSymbols(): Flow<List<Symbol>> {
        return symbolDao.getAllSymbols()
    }

    override fun getFavorites(): Flow<List<Symbol>> {
        return favoriteDao.getFavorites()
    }

    override fun getRecents(limit: Int): Flow<List<Symbol>> {
        return recentDao.getRecents(limit)
    }

    override suspend fun toggleFavorite(unicode: String) {
        if (favoriteDao.isFavorite(unicode)) {
            favoriteDao.removeFavorite(unicode)
        } else {
            favoriteDao.addFavorite(FavoriteEntity(symbolUnicode = unicode))
        }
    }

    override suspend fun isFavorite(unicode: String): Boolean {
        return favoriteDao.isFavorite(unicode)
    }

    override suspend fun addRecent(unicode: String) {
        recentDao.addRecent(RecentEntity(symbolUnicode = unicode))
    }

    override suspend fun getSymbolCount(): Int {
        return symbolDao.getSymbolCount()
    }

    override suspend fun loadSymbolsFromAssets() {
        val count = symbolDao.getSymbolCount()
        if (count == 0) {
            val symbols = symbolParser.parseSymbolsFromAssets()
            symbolDao.insertAll(symbols)
        }
    }

    override suspend fun clearRecents() {
        recentDao.clearRecents()
    }
}
