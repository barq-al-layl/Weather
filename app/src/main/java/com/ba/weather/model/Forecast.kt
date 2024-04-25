package com.ba.weather.model

import java.time.LocalDateTime
import java.time.ZoneId

data class Forecast(
    val locationName: String,
    val timeZoneId: ZoneId,
    val dateTime: LocalDateTime,
    val weather: CurrentWeatherInfo,
    val dailyWeather: List<DailyWeatherInfo>,
    val hourly: List<HourlyWeatherInfo>,
)
