package com.ba.weather.di

import android.content.Context
import androidx.room.Room
import com.ba.weather.BuildConfig
import com.ba.weather.data.api.WeatherService
import com.ba.weather.data.database.CityDao
import com.ba.weather.data.database.WeatherDatabase
import com.ba.weather.model.AlertDialogManager
import com.ba.weather.model.SnackbarManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationsModule {

    @Provides
    @Singleton
    fun provideWeatherService(): WeatherService {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val url = it.request().url.newBuilder()
                    .addQueryParameter("key", BuildConfig.API_KEY)
                    .build()
                val newRequest = it.request().newBuilder()
                    .url(url)
                    .build()
                it.proceed(newRequest)
            }
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
            .build()
        return Retrofit.Builder()
            .baseUrl(" http://api.weatherapi.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create()
    }

    @Provides
    @Singleton
    fun provideWeatherDatabase(@ApplicationContext context: Context): WeatherDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = WeatherDatabase::class.java,
            name = WeatherDatabase.DATABASE_NAME,
        ).build()
    }

    @Provides
    @Singleton
    fun provideCityDao(database: WeatherDatabase): CityDao = database.cityDao

    @Provides
    @Singleton
    fun provideSnackbarManager(): SnackbarManager = SnackbarManager

    @Provides
    @Singleton
    fun provideAlertDialogManager(): AlertDialogManager = AlertDialogManager
}