package com.symbolkeyboard.ui.keyboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.symbolkeyboard.data.model.Symbol

@Composable
fun RecentsRow(
    recents: List<Symbol>,
    onClick: (Symbol) -> Unit,
    modifier: Modifier = Modifier
) {
    if (recents.isNotEmpty()) {
        Column(modifier = modifier.padding(vertical = 4.dp)) {
            Text(
                text = "Recent",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(
                    items = recents.take(15),
                    key = { it.unicode }
                ) { symbol ->
                    SymbolButton(
                        symbol = symbol,
                        onClick = { onClick(symbol) },
                        modifier = Modifier.height(48.dp)
                    )
                }
            }
        }
    }
}
