package com.symbolkeyboard.keyboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.symbolkeyboard.data.model.Symbol
import com.symbolkeyboard.data.repository.SymbolRepository
import com.symbolkeyboard.util.Constants
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class KeyboardViewModel(
    private val repository: SymbolRepository
) : ViewModel() {

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

    private var isInitialized = false

    init {
        observeFavorites()
        observeRecents()
    }

    fun loadSymbols() {
        if (isInitialized) return
        viewModelScope.launch {
            repository.init()
            _totalPages.value = repository.getTotalPages()
            loadCurrentPage()
            isInitialized = true
        }
    }

    private fun loadCurrentPage() {
        val page = _currentPage.value
        val query = _searchQuery.value
        val category = _currentCategory.value

        val result = when {
            query.isNotBlank() -> {
                val all = repository.searchSymbols(query)
                val start = page * Constants.PAGE_SIZE
                val end = minOf(start + Constants.PAGE_SIZE, all.size)
                _totalPages.value = maxOf((all.size + Constants.PAGE_SIZE - 1) / Constants.PAGE_SIZE, 1)
                if (start < all.size) all.subList(start, end) else emptyList()
            }
            category.isNotBlank() -> {
                val all = repository.getSymbolsByCategory(category)
                val start = page * Constants.PAGE_SIZE
                val end = minOf(start + Constants.PAGE_SIZE, all.size)
                _totalPages.value = maxOf((all.size + Constants.PAGE_SIZE - 1) / Constants.PAGE_SIZE, 1)
                if (start < all.size) all.subList(start, end) else emptyList()
            }
            else -> {
                _totalPages.value = repository.getTotalPages()
                repository.getPage(page)
            }
        }
        _symbols.value = result
    }

    fun setCategory(category: String) {
        _currentCategory.value = category
        _searchQuery.value = ""
        _currentPage.value = 0
        loadCurrentPage()
    }

    fun search(query: String) {
        _searchQuery.value = query
        _currentCategory.value = ""
        _currentPage.value = 0
        loadCurrentPage()
    }

    fun nextPage() {
        if (_currentPage.value < _totalPages.value - 1) {
            _currentPage.value = _currentPage.value + 1
            loadCurrentPage()
        }
    }

    fun prevPage() {
        if (_currentPage.value > 0) {
            _currentPage.value = _currentPage.value - 1
            loadCurrentPage()
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
            repository.getRecents(Constants.RECENTS_LIMIT).collect { list ->
                _recents.value = list
            }
        }
    }

    fun clear() {
        _symbols.value = emptyList()
        _favorites.value = emptyList()
        _recents.value = emptyList()
        repository.clearCache()
        isInitialized = false
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
