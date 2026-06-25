package com.symbolkeyboard.data.model

data class Symbol(
    val unicode: String,
    val char: String,
    val name: String,
    val category: String,
    val block: String,
    val requiredFontFamily: String,
    val page: Int = 0
)
