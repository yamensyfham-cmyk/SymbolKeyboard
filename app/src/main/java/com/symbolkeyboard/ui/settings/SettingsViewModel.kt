package com.symbolkeyboard.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.symbolkeyboard.data.datastore.UserPreferencesStore
import com.symbolkeyboard.data.model.Category
import com.symbolkeyboard.data.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesStore: UserPreferencesStore
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { preferencesStore.setDarkTheme(enabled) }
    }

    fun setDynamicColors(enabled: Boolean) {
        viewModelScope.launch { preferencesStore.setDynamicColors(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesStore.setVibrationEnabled(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesStore.setSoundEnabled(enabled) }
    }

    fun setCategoryEnabled(category: String, enabled: Boolean) {
        viewModelScope.launch {
            val current = preferences.value.enabledCategories.toMutableSet()
            if (enabled) current.add(category) else current.remove(category)
            preferencesStore.setEnabledCategories(current)
        }
    }

    fun clearAllData() {
        viewModelScope.launch { preferencesStore.clearAll() }
    }

    val categories = Category.entries
}
