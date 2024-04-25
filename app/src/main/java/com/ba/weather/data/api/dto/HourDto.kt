package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HourDto(
    @Json(name = "time")
    val time: String,
    @Json(name = "temp_c")
    val temp: Double,
    @Json(name = "condition")
    val condition: ConditionDto,
)