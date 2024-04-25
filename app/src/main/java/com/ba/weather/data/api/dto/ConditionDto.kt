package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConditionDto(
    @Json(name = "text")
    val text: String,
    @Json(name = "icon")
    val icon: String,
)