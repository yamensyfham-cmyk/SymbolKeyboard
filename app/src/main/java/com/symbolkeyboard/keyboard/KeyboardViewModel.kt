package com.symbolkeyboard.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.symbolkeyboard.data.model.Symbol
import com.symbolkeyboard.data.repository.SymbolRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class KeyboardViewModel(
    private val repository: SymbolRepository
) : ViewModel() {

    private val _symbols = MutableStateFlow<List<Symbol>>(emptyList())
    val symbols: StateFlow<List<Symbol>> = _symbols.asStateFlow()

    private val _favorites = MutableStateFlow<List<Symbol>>(emptyList())
    val favorites: StateFlow<List<Symbol>> = _favorites.asStateFlow()

    private val _recents = MutableStateFlow<List<Symbol>>(emptyList())
    val recents: StateFlow<List<Symbol>> = _recents.asStateFlow()

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var symbolsJob: Job? = null

    init {
        observeSymbols()
        observeFavorites()
        observeRecents()
    }

    fun loadSymbols() {
        viewModelScope.launch {
            repository.loadSymbolsFromAssets()
        }
    }

    private fun observeSymbols() {
        viewModelScope.launch {
            _currentCategory.flatMapLatest { category ->
                if (category.isBlank()) {
                    repository.getAllSymbols()
                } else {
                    repository.getSymbolsByCategory(category)
                }
            }.collect { list ->
                _symbols.value = list
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavorites().collect { list ->
                _favorites.value = list
            }
        }
    }

    private fun observeRecents() {
        viewModelScope.launch {
            repository.getRecents(30).collect { list ->
                _recents.value = list
            }
        }
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
    }

    fun search(query: String) {
        _searchQuery.value = query
        symbolsJob?.cancel()
        symbolsJob = viewModelScope.launch {
            val flow = if (query.isBlank()) {
                val cat = _currentCategory.value
                if (cat.isBlank()) repository.getAllSymbols()
                else repository.getSymbolsByCategory(cat)
            } else {
                repository.searchSymbols(query)
            }
            flow.collect { list ->
                _symbols.value = list
            }
        }
    }

    fun toggleFavorite(unicode: String) {
        viewModelScope.launch {
            repository.toggleFavorite(unicode)
        }
    }

    fun addRecent(unicode: String) {
        viewModelScope.launch {
            repository.addRecent(unicode)
        }
    }

    fun clear() {
        symbolsJob?.cancel()
        _symbols.value = emptyList()
        _favorites.value = emptyList()
        _recents.value = emptyList()
    }
}

class KeyboardViewModelFactory(
    private val repository: SymbolRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return KeyboardViewModel(repository) as T
    }
}
