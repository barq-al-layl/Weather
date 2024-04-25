package com.ba.weather.model

import java.time.LocalDate

data class DailyWeatherInfo(
    val date: LocalDate,
    val maxTemperature: Int,
    val minTemperature: Int,
    val icon: String,
)
