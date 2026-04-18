// data/repository/WeatherRepositoryImpl.kt
package com.example.weatherapp.data.repository

import com.example.weatherapp.data.remote.WeatherApi
import com.example.weatherapp.data.remote.dto.toDailyForecasts
import com.example.weatherapp.data.remote.dto.toDomain
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> {
        return try {
            val response = api.getWeatherByCity(city.trim())
            Result.success(response.toDomain())
        } catch (e: retrofit2.HttpException) {
            val message = when (e.code()) {
                401 -> "Invalid API key. Please check your key."
                404 -> "City not found. Please try another city."
                429 -> "Too many requests. Please wait a moment."
                else -> "Server error (${e.code()}). Please try again."
            }
            Result.failure(Exception(message))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No internet connection. Please check your network."))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
    }
    override suspend fun getForecastByCity(city: String): Result<List<DailyForecast>> {
        return try {
            val response = api.getForecastByCity(city.trim())
            Result.success(response.toDailyForecasts())
        } catch (e: retrofit2.HttpException) {
            Result.failure(Exception("Forecast error: ${e.code()}"))
        } catch (e: java.net.UnknownHostException) {
            Result.failure(Exception("No internet connection."))
        } catch (e: Exception) {
            Result.failure(Exception("Something went wrong: ${e.message}"))
        }
    }
}