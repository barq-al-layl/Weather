package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    @Json(name = "location")
    val location: LocationDto,
    @Json(name = "current")
    val current: CurrentDto,
    @Json(name = "forecast")
    val forecast: ForecastDto,
)