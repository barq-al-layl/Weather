package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastdayDto(
    @Json(name = "date")
    val date: String,
    @Json(name = "day")
    val day: DayDto,
    @Json(name = "astro")
    val astro: AstroDto,
    @Json(name = "hour")
    val hour: List<HourDto>,
)