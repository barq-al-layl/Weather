package com.ba.weather.data.api.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AstroDto(
    @Json(name = "is_moon_up")
    val isMoonUp: Int,
    @Json(name = "is_sun_up")
    val isSunUp: Int,
)