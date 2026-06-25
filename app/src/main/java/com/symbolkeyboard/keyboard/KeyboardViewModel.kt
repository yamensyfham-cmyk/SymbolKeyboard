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

    companion object {
        const val PAGE_SIZE = 60
    }

    private val _symbols = MutableStateFlow<List<Symbol>>(emptyList())
    val symbols: StateFlow<List<Symbol>> = _symbols.asStateFlow()

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    private val _totalPages = MutableStateFlow(0)
    val totalPages: StateFlow<Int> = _totalPages.asStateFlow()

    private val _favorites = MutableStateFlow<List<Symbol>>(emptyList())
    val favorites: StateFlow<List<Symbol>> = _favorites.asStateFlow()

    private val _recents = MutableStateFlow<List<Symbol>>(emptyList())
    val recents: StateFlow<List<Symbol>> = _recents.asStateFlow()

    private val _currentCategory = MutableStateFlow("")
    val currentCategory: StateFlow<String> = _currentCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var allSymbols: List<Symbol> = emptyList()

    init {
        observeFavorites()
        observeRecents()
    }

    fun loadSymbols() {
        viewModelScope.launch {
            repository.loadSymbolsFromAssets()
            repository.getAllSymbols().collect { list ->
                allSymbols = list
                applyFiltersAndPaginate()
            }
        }
    }

    private fun applyFiltersAndPaginate() {
        val query = _searchQuery.value
        val category = _currentCategory.value

        val filtered = when {
            query.isNotBlank() -> allSymbols.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.unicode.contains(query, ignoreCase = true)
            }
            category.isNotBlank() -> allSymbols.filter { it.category == category }
            else -> allSymbols
        }

        _totalPages.value = maxOf((filtered.size + PAGE_SIZE - 1) / PAGE_SIZE, 1)

        if (_currentPage.value >= _totalPages.value) {
            _currentPage.value = maxOf(_totalPages.value - 1, 0)
        }

        val start = _currentPage.value * PAGE_SIZE
        val end = minOf(start + PAGE_SIZE, filtered.size)
        _symbols.value = if (start < filtered.size) filtered.subList(start, end) else emptyList()
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
        _currentPage.value = 0
        applyFiltersAndPaginate()
    }

    fun search(query: String) {
        _searchQuery.value = query
        _currentPage.value = 0
        applyFiltersAndPaginate()
    }

    fun nextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value = _currentPage.value + 1
            applyFiltersAndPaginate()
        }
    }

    fun prevPage() {
        if (_currentPage.value > 0) {
            _currentPage.value = _currentPage.value - 1
            applyFiltersAndPaginate()
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

    fun clear() {
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
