package com.symbolkeyboard.ui.keyboard

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.symbolkeyboard.data.model.Category
import com.symbolkeyboard.keyboard.KeyboardViewModel
import com.symbolkeyboard.util.PowerSaver

@Composable
fun KeyboardContent(
    viewModel: KeyboardViewModel?,
    powerSaver: PowerSaver,
    onSymbolClick: (String) -> Unit
) {
    val symbols by viewModel?.symbols?.collectAsState()
        ?: rememberSaveable { mutableStateOf(emptyList()) }
    val favorites by viewModel?.favorites?.collectAsState()
        ?: rememberSaveable { mutableStateOf(emptyList()) }
    val recents by viewModel?.recents?.collectAsState()
        ?: rememberSaveable { mutableStateOf(emptyList()) }
    var selectedCategory by rememberSaveable { mutableStateOf(Category.MISCELLANEOUS.key) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        SearchBar(
            query = viewModel?.searchQuery?.value ?: "",
            onQueryChange = { viewModel?.search(it) }
        )

        CategoryTabs(
            categories = Category.entries.map { it.key },
            selectedCategory = selectedCategory,
            onCategorySelected = {
                selectedCategory = it
                viewModel?.setCategory(it)
            }
        )

        if (recents.isNotEmpty() && (viewModel?.searchQuery?.value?.isBlank() ?: true)) {
            RecentsRow(
                recents = recents,
                onClick = { symbol ->
                    onSymbolClick(symbol.char)
                    viewModel?.addRecent(symbol.char)
                }
            )
        }

        SymbolGrid(
            symbols = symbols,
            favorites = favorites.map { it.unicode }.toSet(),
            onSymbolClick = { symbol ->
                onSymbolClick(symbol.char)
                viewModel?.addRecent(symbol.char)
            },
            onSymbolLongClick = { symbol ->
                viewModel?.toggleFavorite(symbol.unicode)
                Toast.makeText(
                    context,
                    if (favorites.any { it.unicode == symbol.unicode })
                        "Removed from favorites"
                    else
                        "Added to favorites",
                    Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}
