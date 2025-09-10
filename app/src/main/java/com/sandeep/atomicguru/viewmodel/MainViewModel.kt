package com.sandeep.atomicguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.data.ElementRepository
import com.sandeep.atomicguru.data.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class Language { EN, OD }
enum class TableView { GRID, CLASSIC }

data class AppState(
    val allElements: List<Element> = emptyList(),
    val filteredElements: List<Element> = emptyList(),
    val searchQuery: String = "",
    val currentLanguage: Language = Language.OD,
    val favoriteIds: Set<String> = emptySet(),
    val tableView: TableView = TableView.GRID
)

class MainViewModel(private val repository: ElementRepository) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        loadElements()
        loadFavorites()
    }

    private fun loadElements() {
        viewModelScope.launch {
            val elements = repository.allElements
            _state.update { it.copy(allElements = elements, filteredElements = elements) }
        }
    }

    private fun loadFavorites() {
        val favIds = repository.getFavoriteElementIds()
        _state.update { it.copy(favoriteIds = favIds) }
    }

    fun onSearchQueryChanged(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterElements(query)
    }

    fun setLanguage(newLanguage: Language, userPreferences: UserPreferences) {
        _state.update { it.copy(currentLanguage = newLanguage) }
        userPreferences.saveLanguage(newLanguage)
    }

    fun toggleFavorite(atomicNumber: Int) {
        repository.toggleFavorite(atomicNumber)
        loadFavorites()
    }

    fun getElementByNumber(atomicNumber: Int): Element? {
        return _state.value.allElements.find { it.atomicNumber == atomicNumber }
    }

    fun getFavoriteElements(): List<Element> {
        return _state.value.allElements.filter {
            _state.value.favoriteIds.contains(it.atomicNumber.toString())
        }
    }

    fun toggleTableView() {
        val newView = if (_state.value.tableView == TableView.GRID) TableView.CLASSIC else TableView.GRID
        _state.update { it.copy(tableView = newView) }
    }

    private fun filterElements(query: String) {
        val filtered = if (query.isBlank()) {
            _state.value.allElements
        } else {
            _state.value.allElements.filter { element ->
                // Check if the transliterated name exists and contains the query
                val nameOeMatches = element.name_oe?.contains(query, ignoreCase = true) ?: false

                // The final search condition
                element.name.contains(query, ignoreCase = true) ||
                        element.detailsOdia.generalInfo.elementName.contains(query, ignoreCase = true) ||
                        nameOeMatches || // <-- ADDED THE NEW SEARCH CONDITION
                        element.symbol.contains(query, ignoreCase = true) ||
                        element.atomicNumber.toString().contains(query)
            }
        }
        _state.update { it.copy(filteredElements = filtered) }
    }
}