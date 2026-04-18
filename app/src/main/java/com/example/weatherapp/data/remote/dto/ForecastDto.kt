package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// الـ Response الكامل من الـ API
data class ForecastResponseDto(
    val list: List<ForecastItemDto>,  // قائمة كل 3 ساعات
    val city: CityDto
)

// كل عنصر في القائمة = 3 ساعات
data class ForecastItemDto(
    @SerializedName("dt") val timestamp: Long,       // Unix timestamp
    val main: ForecastMainDto,
    val weather: List<ForecastWeatherDto>,
    val wind: ForecastWindDto,
    @SerializedName("dt_txt") val dateText: String   // مثل "2024-01-15 12:00:00"
)

data class ForecastMainDto(
    val temp: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val humidity: Int
)

data class ForecastWeatherDto(
    val main: String,
    val description: String,
    val icon: String
)

data class ForecastWindDto(
    val speed: Double
)

data class CityDto(
    val name: String,
    val country: String
)