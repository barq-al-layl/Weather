package com.ba.weather.data.database

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WeatherDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    val favouriteCityId = getValue(FAVOURITE_LOCATION_ID).filterNotNull()
    suspend fun setFavouriteCityId(id: Int) = setValue(FAVOURITE_LOCATION_ID, id)

    fun <T> getValue(key: Preferences.Key<T>): Flow<T?> {
        return context.dataStore.data.map { preferences ->
            preferences[key]
        }
    }

    suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
        private const val DATA_STORE_NAME = "weather_datastore"
        val Context.dataStore by preferencesDataStore(DATA_STORE_NAME)

        val FAVOURITE_LOCATION_ID = intPreferencesKey("favourite_location")
    }
}