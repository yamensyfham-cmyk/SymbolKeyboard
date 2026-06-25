package com.symbolkeyboard.util

import android.content.Context
import com.symbolkeyboard.data.model.Symbol
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SymbolParser @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var indexCache: List<Symbol>? = null
    private var totalPagesCache: Int = 0

    fun loadIndex(): List<Symbol> {
        indexCache?.let { return it }

        val jsonString = readAsset(Constants.INDEX_FILE)
        val root = JSONObject(jsonString)
        totalPagesCache = root.getInt("totalPages")
        val symbolsArray = root.getJSONArray("symbols")
        val symbols = mutableListOf<Symbol>()

        for (i in 0 until symbolsArray.length()) {
            val obj = symbolsArray.getJSONObject(i)
            symbols.add(Symbol(
                unicode = obj.getString("unicode"),
                char = obj.getString("char"),
                name = obj.getString("name"),
                category = obj.getString("category"),
                block = obj.getString("block"),
                requiredFontFamily = obj.optString("required_font_family", ""),
                page = obj.getInt("page")
            ))
        }

        indexCache = symbols
        return symbols
    }

    fun getTotalPages(): Int {
        if (totalPagesCache == 0) loadIndex()
        return totalPagesCache
    }

    fun loadPage(pageNumber: Int): List<Symbol> {
        val fileName = String.format(Constants.PAGE_FILE_FORMAT, pageNumber)
        val jsonString = readAsset(fileName)
        val jsonArray = JSONArray(jsonString)
        val symbols = mutableListOf<Symbol>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            symbols.add(Symbol(
                unicode = obj.getString("unicode"),
                char = obj.getString("char"),
                name = obj.getString("name"),
                category = obj.getString("category"),
                block = obj.getString("block"),
                requiredFontFamily = obj.optString("required_font_family", ""),
                page = pageNumber
            ))
        }

        return symbols
    }

    fun getSymbolByUnicode(unicode: String): Symbol? {
        val index = loadIndex()
        return index.find { it.unicode == unicode }
    }

    fun searchSymbols(query: String): List<Symbol> {
        val index = loadIndex()
        return index.filter {
            it.name.contains(query, ignoreCase = true) ||
            it.unicode.contains(query, ignoreCase = true)
        }
    }

    fun getSymbolsByCategory(category: String): List<Symbol> {
        val index = loadIndex()
        return index.filter { it.category == category }
    }

    fun clearCache() {
        indexCache = null
        totalPagesCache = 0
    }

    private fun readAsset(path: String): String {
        val inputStream = context.assets.open(path)
        val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
        val stringBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            stringBuilder.append(line)
        }
        reader.close()
        return stringBuilder.toString()
    }
}
