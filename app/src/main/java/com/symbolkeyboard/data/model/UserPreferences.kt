package com.symbolkeyboard.data.model

data class UserPreferences(
    val darkTheme: Boolean = false,
    val dynamicColors: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = false,
    val enabledCategories: Set<String> = Category.entries.map { it.key }.toSet()
)
