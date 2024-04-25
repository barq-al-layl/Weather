package com.ba.weather.data.repository

import com.ba.weather.data.api.dto.*
import com.ba.weather.data.database.entity.CityEntity
import com.ba.weather.model.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val dateTimeFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") }

fun ForecastResponse.toForecast() = Forecast(
    locationName = location.name,
    timeZoneId = ZoneId.of(location.tzId),
    dateTime = LocalDateTime.parse(
        current.lastUpdated, dateTimeFormatter,
    ),
    weather = current.toCurrentWeatherInfo(forecast.forecastday.first().day.dailyChanceOfRain),
    dailyWeather = forecast.forecastday.map { it.toDailyWeatherInfo() },
    hourly = forecast.forecastday.first().hour.map { it.toHourlyWeatherInfo() },
)

fun CurrentDto.toCurrentWeatherInfo(chanceOfRain: Int) = CurrentWeatherInfo(
    temperature = temp.roundToInt(),
    windSpeed = windSpeed.roundToInt(),
    humidity = humidity,
    chanceOfRain = chanceOfRain,
    icon = "http:${condition.icon.replace("64x64", "128x128")}",
)

fun ForecastdayDto.toDailyWeatherInfo() = DailyWeatherInfo(
    date = LocalDate.parse(date),
    maxTemperature = day.maxTemp.roundToInt(),
    minTemperature = day.minTemp.roundToInt(),
    icon = "http:${day.condition.icon.replace("64x64", "128x128")}",
)

fun HourDto.toHourlyWeatherInfo() = HourlyWeatherInfo(
    time = LocalTime.parse(time, dateTimeFormatter),
    temperature = temp.roundToInt(),
    icon = "http:${condition.icon}",
)

fun SearchItemDto.toCity() = City(
    id = id,
    name = name,
    country = country,
    latitude = lat,
    longitude = lon,
)

fun CityEntity.toCity() = City(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
)

fun City.toEntity() = CityEntity(
    id = id,
    name = name,
    country = country,
    latitude = latitude,
    longitude = longitude,
)
