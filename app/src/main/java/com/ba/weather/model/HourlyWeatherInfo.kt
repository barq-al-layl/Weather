package com.ba.weather.model

import java.time.LocalTime

data class HourlyWeatherInfo(
    val time: LocalTime,
    val temperature: Int,
    val icon: String,
)
