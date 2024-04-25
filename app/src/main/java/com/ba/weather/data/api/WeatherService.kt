package com.ba.weather.data.api

import com.ba.weather.data.api.dto.ForecastResponse
import com.ba.weather.data.api.dto.SearchItemDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") query: String,
        @Query("days") days: Int = 7,
    ): ForecastResponse

    @GET("search.json")
    suspend fun getCities(
        @Query("q") query: String,
    ): List<SearchItemDto>
}