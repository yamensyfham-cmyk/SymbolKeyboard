package com.symbolkeyboard.data.datastore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.symbolkeyboard.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesStore(private val dataStore: androidx.datastore.core.DataStore<Preferences>) {

    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val dynamicColorsKey = booleanPreferencesKey("dynamic_colors")
    private val vibrationEnabledKey = booleanPreferencesKey("vibration_enabled")
    private val soundEnabledKey = booleanPreferencesKey("sound_enabled")
    private val enabledCategoriesKey = stringSetPreferencesKey("enabled_categories")

    val preferences: Flow<UserPreferences> = dataStore.data.map { prefs ->
        UserPreferences(
            darkTheme = prefs[darkThemeKey] ?: false,
            dynamicColors = prefs[dynamicColorsKey] ?: true,
            vibrationEnabled = prefs[vibrationEnabledKey] ?: true,
            soundEnabled = prefs[soundEnabledKey] ?: false,
            enabledCategories = prefs[enabledCategoriesKey] ?: emptySet()
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[darkThemeKey] = enabled }
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { it[dynamicColorsKey] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[vibrationEnabledKey] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[soundEnabledKey] = enabled }
    }

    suspend fun setEnabledCategories(categories: Set<String>) {
        dataStore.edit { it[enabledCategoriesKey] = categories }
    }

    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}
