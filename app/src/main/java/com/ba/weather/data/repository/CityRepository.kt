package com.ba.weather.data.repository

import com.ba.weather.data.api.WeatherService
import com.ba.weather.data.database.CityDao
import com.ba.weather.data.database.WeatherDataStore
import com.ba.weather.model.City
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CityRepository @Inject constructor(
    private val weatherService: WeatherService,
    private val weatherDataStore: WeatherDataStore,
    private val cityDao: CityDao,
) {

    fun getSavedCitiesStream(): Flow<List<City>> {
        return cityDao.getCitiesStream()
            .map { cities ->
                cities.map { it.toCity() }
            }
    }

    fun getFavouriteCityStream(): Flow<City?> {
        return getSavedCitiesStream()
            .combine(weatherDataStore.favouriteCityId) { cities, id ->
                cities.find { it.id == id }
            }
    }

    suspend fun setCityAsFavourite(city: City) {
        weatherDataStore.setFavouriteCityId(city.id)
    }

    suspend fun getSearchResult(query: String): List<City> {
        return weatherService.getCities(query)
            .map { it.toCity() }
    }

    suspend fun saveCity(city: City) {
        cityDao.insert(city.toEntity())
    }

    suspend fun removeCity(city: City) {
        cityDao.delete(city.toEntity())
    }
}