package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CurrentDto(
    @Json(name = "last_updated")
    val lastUpdated: String,
    @Json(name = "temp_c")
    val temp: Double,
    @Json(name = "condition")
    val condition: ConditionDto,
    @Json(name = "wind_kph")
    val windSpeed: Double,
    @Json(name = "humidity")
    val humidity: Int,
    @Json(name = "cloud")
    val cloud: Int,
    @Json(name = "feelslike_c")
    val feelsLike: Double,
    @Json(name = "uv")
    val uv: Double,
)