package com.ba.weather.data.database

import androidx.room.Dao
import androidx.room.Query
import com.ba.weather.data.database.entity.CityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao : BaseDao<CityEntity> {

    @Query("SELECT * FROM cities")
    fun getCitiesStream(): Flow<List<CityEntity>>
}