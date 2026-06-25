package com.symbolkeyboard.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "symbols",
    indices = [Index(value = ["category"]), Index(value = ["block"])]
)
data class Symbol(
    @PrimaryKey
    val unicode: String,
    val char: String,
    val name: String,
    val category: String,
    val block: String,
    val requiredFontFamily: String
)
