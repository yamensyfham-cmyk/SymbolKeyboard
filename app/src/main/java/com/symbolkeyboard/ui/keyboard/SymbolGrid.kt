package com.symbolkeyboard.ui.keyboard

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.symbolkeyboard.data.model.Symbol
import com.symbolkeyboard.util.Constants

@Composable
fun SymbolGrid(
    symbols: List<Symbol>,
    favorites: Set<String>,
    onSymbolClick: (Symbol) -> Unit,
    onSymbolLongClick: (Symbol) -> Unit,
    modifier: Modifier = Modifier
) {
    val gridState = rememberLazyGridState()

    LazyVerticalGrid(
        columns = GridCells.Fixed(Constants.GRID_COLUMNS),
        state = gridState,
        modifier = modifier,
        userScrollEnabled = true
    ) {
        items(
            items = symbols,
            key = { it.unicode }
        ) { symbol ->
            SymbolButton(
                symbol = symbol,
                isFavorite = favorites.contains(symbol.unicode),
                onClick = { onSymbolClick(symbol) },
                onLongClick = { onSymbolLongClick(symbol) }
            )
        }
    }
}
