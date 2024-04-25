package com.ba.weather.model

data class CurrentWeatherInfo(
    val temperature: Int,
    val windSpeed: Int,
    val humidity: Int,
    val chanceOfRain: Int,
    val icon: String,
)
