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

    data class RawSymbol(
        val unicode: String,
        val char: String,
        val name: String,
        val category: String,
        val block: String,
        val requiredFontFamily: String
    )

    fun parseSymbolsFromAssets(): List<Symbol> {
        val jsonString = readJsonFromAssets()
        val jsonArray = JSONArray(jsonString)
        val symbols = mutableListOf<Symbol>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val symbol = Symbol(
                unicode = obj.getString("unicode"),
                char = obj.getString("char"),
                name = obj.getString("name"),
                category = obj.getString("category"),
                block = obj.getString("block"),
                requiredFontFamily = obj.optString("required_font_family", "")
            )
            symbols.add(symbol)
        }

        return symbols
    }

    private fun readJsonFromAssets(): String {
        val inputStream = context.assets.open(Constants.SYMBOLS_JSON_FILE)
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
