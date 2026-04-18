// domain/repository/WeatherRepository.kt
package com.example.weatherapp.domain.repository

import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Result<Weather>
    suspend fun getForecastByCity(city: String): Result<List<DailyForecast>>

}