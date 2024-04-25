package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DayDto(
    @Json(name = "maxtemp_c")
    val maxTemp: Double,
    @Json(name = "mintemp_c")
    val minTemp: Double,
    @Json(name = "daily_chance_of_rain")
    val dailyChanceOfRain: Int,
    @Json(name = "condition")
    val condition: ConditionDto,
)