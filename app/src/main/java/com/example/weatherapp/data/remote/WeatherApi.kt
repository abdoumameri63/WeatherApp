// data/remote/WeatherApi.kt
package com.example.weatherapp.data.remote

import com.example.weatherapp.data.remote.dto.ForecastResponseDto
import com.example.weatherapp.data.remote.dto.WeatherDto
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("weather")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): WeatherDto
    @GET("forecast")
    suspend fun getForecastByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String = API_KEY,
        @Query("units") units: String = "metric"
    ): ForecastResponseDto

    companion object {
        const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
        // ⚠️ Replace with your free key from openweathermap.org/api
        const val API_KEY = "9171ded9840992dad3eb427a9658f193"
    }
}