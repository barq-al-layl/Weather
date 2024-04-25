package com.ba.weather.data.repository

import com.ba.weather.data.api.WeatherService
import com.ba.weather.model.Forecast
import javax.inject.Inject

class ForecastRepository @Inject constructor(
    private val weatherService: WeatherService,
) {
    suspend fun getForecast(latitude: Double, longitude: Double): Forecast {
        return weatherService.getForecast(
            query = "$latitude,$longitude",
        ).toForecast()
    }
}