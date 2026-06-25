package com.symbolkeyboard.ui.keyboard

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.symbolkeyboard.data.model.Category
import com.symbolkeyboard.keyboard.KeyboardViewModel
import com.symbolkeyboard.util.PowerSaver

@Composable
fun KeyboardContent(
    viewModel: KeyboardViewModel,
    powerSaver: PowerSaver,
    onSymbolClick: (String) -> Unit
) {
    val symbols by viewModel.symbols.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val recents by viewModel.recents.collectAsState()
    val currentPage by viewModel.currentPage.collectAsState()
    val totalPages by viewModel.totalPages.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.search(it) }
        )

        CategoryTabs(
            categories = Category.entries.map { it.key },
            selectedCategory = viewModel.currentCategory.value,
            onCategorySelected = { viewModel.setCategory(it) }
        )

        if (recents.isNotEmpty() && searchQuery.isBlank()) {
            RecentsRow(
                recents = recents,
                onClick = { symbol ->
                    onSymbolClick(symbol.char)
                    viewModel.addRecent(symbol.unicode)
                }
            )
        }

        SymbolGrid(
            symbols = symbols,
            favorites = favorites.map { it.unicode }.toSet(),
            onSymbolClick = { symbol ->
                onSymbolClick(symbol.char)
                viewModel.addRecent(symbol.unicode)
            },
            onSymbolLongClick = { symbol ->
                viewModel.toggleFavorite(symbol.unicode)
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.prevPage() },
                enabled = currentPage > 0,
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Previous page"
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "${currentPage + 1} / $totalPages",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { viewModel.nextPage() },
                enabled = currentPage < totalPages - 1,
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next page"
                )
            }
        }
    }
}
