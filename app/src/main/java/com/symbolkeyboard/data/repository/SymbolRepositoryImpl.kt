package com.symbolkeyboard.data.repository

import com.symbolkeyboard.data.local.FavoriteDao
import com.symbolkeyboard.data.local.FavoriteEntity
import com.symbolkeyboard.data.local.RecentDao
import com.symbolkeyboard.data.local.RecentEntity
import com.symbolkeyboard.data.model.Symbol
import com.symbolkeyboard.util.SymbolParser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao,
    private val recentDao: RecentDao,
    private val symbolParser: SymbolParser
) : SymbolRepository {

    private val pageCache = ConcurrentHashMap<Int, List<Symbol>>()
    private var totalPages = 0

    override suspend fun init() {
        totalPages = symbolParser.getTotalPages()
        loadPage(0)
    }

    override fun getPage(pageNumber: Int): List<Symbol> {
        return pageCache.getOrPut(pageNumber) {
            symbolParser.loadPage(pageNumber)
        }
    }

    override fun getTotalPages(): Int = totalPages

    override fun searchSymbols(query: String): List<Symbol> {
        return symbolParser.searchSymbols(query)
    }

    override fun getSymbolsByCategory(category: String): List<Symbol> {
        return symbolParser.getSymbolsByCategory(category)
    }

    override fun getSymbolByUnicode(unicode: String): Symbol? {
        return symbolParser.getSymbolByUnicode(unicode)
    }

    override fun getFavorites(): Flow<List<Symbol>> {
        return favoriteDao.getFavoriteUnicodes().map { unicodes ->
            unicodes.mapNotNull { unicode ->
                symbolParser.getSymbolByUnicode(unicode)
            }
        }
    }

    override fun getRecents(limit: Int): Flow<List<Symbol>> {
        return recentDao.getRecentUnicodes(limit).map { unicodes ->
            unicodes.mapNotNull { unicode ->
                symbolParser.getSymbolByUnicode(unicode)
            }
        }
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

    override suspend fun clearRecents() {
        recentDao.clearRecents()
    }

    override fun clearCache() {
        pageCache.clear()
        symbolParser.clearCache()
    }

    private suspend fun loadPage(pageNumber: Int) {
        pageCache.getOrPut(pageNumber) {
            symbolParser.loadPage(pageNumber)
        }
    }
}
