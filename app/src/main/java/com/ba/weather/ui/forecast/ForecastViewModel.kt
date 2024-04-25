package com.ba.weather.ui.forecast

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ba.weather.R
import com.ba.weather.data.LocationTracker
import com.ba.weather.data.repository.CityRepository
import com.ba.weather.data.repository.ForecastRepository
import com.ba.weather.model.AlertDialogContent
import com.ba.weather.model.AlertDialogManager
import com.ba.weather.model.Forecast
import com.ba.weather.model.SnackbarManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okio.IOException
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class ForecastViewModel @Inject constructor(
    cityRepository: CityRepository,
    private val forecastRepository: ForecastRepository,
    private val snackbarManager: SnackbarManager,
    private val dialogManager: AlertDialogManager,
    private val locationTracker: LocationTracker,
) : ViewModel() {

    private val favouriteLocation = cityRepository.getFavouriteCityStream()
        .onEach { refreshForecast() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    val isLocationAvailable = favouriteLocation.map {
        it != null || locationTracker.isLocationEnabled
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = locationTracker.isLocationEnabled,
    )

    private val isCurrentLocation = MutableStateFlow(false)

    private val _useCurrentLocation = MutableStateFlow(locationTracker.isLocationEnabled)
    val useCurrentLocation = _useCurrentLocation.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _forecast = MutableStateFlow<Forecast?>(null)
    val forecast = _forecast.asStateFlow()

    val currentHour = forecast.mapNotNull {
        it?.let {
            ZonedDateTime.now(it.timeZoneId).hour
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ZonedDateTime.now().hour,
    )

    private var forecastJob: Job? = null

    init {
        refreshForecast()
    }

    fun refreshForecast() {
        forecastJob?.cancel()
        forecastJob = viewModelScope.launch {
            try {
                _isLoading.update { true }
                var latitude: Double? = null
                var longitude: Double? = null
                var location: Location? = null
                if (useCurrentLocation.value) {
                    location = locationTracker.getLocation()
                }
                location?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    isCurrentLocation.update { true }
                } ?: favouriteLocation.value?.let {
                    latitude = it.latitude
                    longitude = it.longitude
                    isCurrentLocation.update { false }
                }
                _forecast.update {
                    forecastRepository.getForecast(
                        latitude = latitude ?: return@update it,
                        longitude = longitude ?: return@update it,
                    )
                }
            } catch (e: IOException) {
                snackbarManager.showMessage(R.string.network_error)
            } finally {
                _isLoading.update { false }
            }
        }
    }

    fun onUseCurrentLocationChanged(value: Boolean) {
        viewModelScope.launch {
            _useCurrentLocation.update { value }
            if (value && !locationTracker.isLocationEnabled) {
                val dialogContent = AlertDialogContent(
                    title = R.string.location_service_is_disabled,
                    body = R.string.please_enable_location_service,
                )
                dialogManager.showDialog(dialogContent)
                _useCurrentLocation.update { false }
            } else {
                refreshForecast()
            }
        }
    }
}