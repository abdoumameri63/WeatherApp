// data/remote/dto/WeatherDto.kt
package com.example.weatherapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class WeatherDto(
    val name: String,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>,
    val wind: WindDto,
    val sys: SysDto,
    val visibility: Int,
    val dt: Long,
    val clouds: CloudsDto,
    val coord: CoordDto
)

data class MainDto(
    val temp: Double,
    @SerializedName("feels_like") val feelsLike: Double,
    @SerializedName("temp_min") val tempMin: Double,
    @SerializedName("temp_max") val tempMax: Double,
    val humidity: Int,
    val pressure: Int
)

data class WeatherDescriptionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class WindDto(
    val speed: Double,
    val deg: Int
)

data class SysDto(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class CloudsDto(
    val all: Int
)

data class CoordDto(
    val lat: Double,
    val lon: Double
)