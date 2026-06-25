package com.symbolkeyboard.ui.settings

import android.content.Context
import android.provider.Settings
import android.view.inputmethod.InputMethodInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.symbolkeyboard.data.datastore.UserPreferencesStore
import com.symbolkeyboard.data.model.Category
import com.symbolkeyboard.data.model.UserPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesStore: UserPreferencesStore,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val preferences: StateFlow<UserPreferences> = preferencesStore.preferences
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UserPreferences()
        )

    private val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

    fun isKeyboardEnabled(): Boolean {
        val enabledIds = imm.enabledInputMethodList.map { it.id }
        return enabledIds.any { it.contains("symbolkeyboard") }
    }

    fun isKeyboardActive(): Boolean {
        val currentId = imm.currentInputMethod?.id ?: return false
        return currentId.contains("symbolkeyboard")
    }

    fun openKeyboardSettings() {
        context.startActivity(
            android.content.Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun openKeyboardPicker() {
        imm.showInputMethodPicker()
    }

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
