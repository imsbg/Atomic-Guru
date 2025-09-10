package com.sandeep.atomicguru.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sandeep.atomicguru.data.Element
import com.sandeep.atomicguru.data.ElementRepository
import com.sandeep.atomicguru.data.Promotion
import com.sandeep.atomicguru.data.UserPreferences
import com.sandeep.atomicguru.network.PromotionApi
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
    val tableView: TableView = TableView.GRID,
    val promotionToShow: Promotion? = null // State to hold the ad
)

class MainViewModel(private val repository: ElementRepository) : ViewModel() {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    init {
        loadElements()
        loadFavorites()
        fetchPromotion() // Fetch the promotion when the ViewModel is created
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

    private fun fetchPromotion() {
        viewModelScope.launch {
            try {
                val response = PromotionApi.retrofitService.getPromotions()
                // If the list is not empty, pick one randomly to show
                if (response.promotions.isNotEmpty()) {
                    _state.update { it.copy(promotionToShow = response.promotions.random()) }
                }
            } catch (e: Exception) {
                // Handle network errors gracefully (e.g., no internet)
                // In this case, we simply don't show a promotion, and promotionToShow will remain null.
                e.printStackTrace()
            }
        }
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
                val nameOeMatches = element.name_oe?.contains(query, ignoreCase = true) ?: false
                element.name.contains(query, ignoreCase = true) ||
                        element.detailsOdia.generalInfo.elementName.contains(query, ignoreCase = true) ||
                        nameOeMatches ||
                        element.symbol.contains(query, ignoreCase = true) ||
                        element.atomicNumber.toString().contains(query)
            }
        }
        _state.update { it.copy(filteredElements = filtered) }
    }
}