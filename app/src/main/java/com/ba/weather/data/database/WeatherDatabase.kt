package com.ba.weather.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ba.weather.data.database.entity.CityEntity

@Database(
    version = 1,
    entities = [CityEntity::class],
    exportSchema = false,
)
abstract class WeatherDatabase : RoomDatabase() {

    abstract val cityDao: CityDao

    companion object {
        const val DATABASE_NAME = "weather_db"
    }
}