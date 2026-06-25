package com.symbolkeyboard.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import com.symbolkeyboard.data.repository.SymbolRepository
import com.symbolkeyboard.ui.keyboard.KeyboardContent
import com.symbolkeyboard.ui.theme.SymbolKeyboardTheme
import com.symbolkeyboard.util.PowerSaver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SymbolKeyboardService : InputMethodService() {

    @Inject
    lateinit var powerSaver: PowerSaver

    @Inject
    lateinit var repository: SymbolRepository

    private lateinit var keyboardViewModel: KeyboardViewModel
    private var composeView: ComposeView? = null
    private var isPasswordField = false

    override fun onCreate() {
        super.onCreate()
        keyboardViewModel = KeyboardViewModel(repository)
        keyboardViewModel.loadSymbols()
    }

    override fun onCreateInputView(): View {
        composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                SymbolKeyboardTheme {
                    if (isPasswordField) {
                        PasswordWarningContent()
                    } else {
                        KeyboardContent(
                            viewModel = keyboardViewModel,
                            powerSaver = powerSaver,
                            onSymbolClick = { char ->
                                commitSymbol(char)
                            }
                        )
                    }
                }
            }
        }
        return composeView!!
    }

    override fun onStartInputView(info: EditorInfo, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        isPasswordField = (info.inputType and EditorInfo.TYPE_MASK_VARIATION) in listOf(
            EditorInfo.TYPE_TEXT_VARIATION_PASSWORD,
            EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD,
            EditorInfo.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT
        )
    }

    override fun onDestroy() {
        keyboardViewModel.clear()
        composeView = null
        super.onDestroy()
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        if (level >= TRIM_MEMORY_MODERATE) {
            keyboardViewModel.clear()
        }
    }

    private fun commitSymbol(char: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.beginBatchEdit()
        inputConnection.commitText(char, 1)
        inputConnection.endBatchEdit()
    }
}

@Composable
fun PasswordWarningContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SymbolKeyboard is disabled in password fields for security.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
