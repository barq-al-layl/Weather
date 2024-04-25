package com.ba.weather.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ba.weather.R
import com.ba.weather.data.repository.CityRepository
import com.ba.weather.model.City
import com.ba.weather.model.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import javax.inject.Inject

data class SearchTextFieldState(
    val query: String = "",
    val error: String = "",
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: CityRepository,
    private val snackbarManager: SnackbarManager,
) : ViewModel() {
    val savedCities = repository.getSavedCitiesStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val favouriteCity = repository.getFavouriteCityStream().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null,
    )

    private val _searchQuery = MutableStateFlow(SearchTextFieldState())
    val searchQuery = _searchQuery.asStateFlow()

    private var remoteCities = emptyList<City>()

    private val _searchResult = MutableStateFlow(emptyList<City>())
    val searchResult = _searchResult.asStateFlow()

    private var searchJob: Job? = null

    private fun getSearchResult() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            try {
                remoteCities = repository.getSearchResult(searchQuery.value.query)
                _searchResult.update {
                    (remoteCities - savedCities.value.toSet())
                        .sortedBy { it.name }
                }
            } catch (_: IOException) {
                snackbarManager.showMessage(R.string.network_error)
            }
        }
    }

    fun saveCity(city: City) {
        viewModelScope.launch {
            _searchResult.update { it - city }
            repository.saveCity(city)
        }
    }

    fun setCityAsFavourite(city: City) {
        viewModelScope.launch {
            saveCity(city)
            repository.setCityAsFavourite(city)
        }
    }

    fun removeCity(city: City) {
        viewModelScope.launch {
            repository.removeCity(city)
            if (remoteCities.contains(city)) {
                _searchResult.update { cities ->
                    (cities + city).sortedBy { it.name }
                }
            }
        }
    }

    fun onSearchQueryValueChanged(value: String) {
        _searchQuery.update { it.copy(query = value, error = "") }
    }

    fun onSearch() {
        if (searchQuery.value.query.length < 3) {
            _searchQuery.update { it.copy(error = "Enter 3 chars at least") }
            return
        }
        getSearchResult()
    }
}