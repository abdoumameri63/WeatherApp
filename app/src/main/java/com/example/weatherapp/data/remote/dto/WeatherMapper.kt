// data/remote/dto/WeatherMapper.kt
package com.example.weatherapp.data.remote.dto

import com.example.weatherapp.domain.model.Weather

fun WeatherDto.toDomain(): Weather = Weather(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    tempMin = main.tempMin,
    tempMax = main.tempMax,
    humidity = main.humidity,
    pressure = main.pressure,
    windSpeed = wind.speed,
    windDegree = wind.deg,
    weatherMain = weather.firstOrNull()?.main ?: "",
    weatherDescription = weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
    weatherIcon = weather.firstOrNull()?.icon ?: "",
    visibility = visibility / 1000, // convert to km
    cloudiness = clouds.all,
    sunrise = sys.sunrise,
    sunset = sys.sunset,
    timestamp = dt,
    latitude = coord.lat,
    longitude = coord.lon
)