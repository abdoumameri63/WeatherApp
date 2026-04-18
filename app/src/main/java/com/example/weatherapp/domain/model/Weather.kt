// domain/model/Weather.kt
package com.example.weatherapp.domain.model

data class Weather(
    val cityName: String,
    val country: String,
    val temperature: Double,
    val feelsLike: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val pressure: Int,
    val windSpeed: Double,
    val windDegree: Int,
    val weatherMain: String,
    val weatherDescription: String,
    val weatherIcon: String,
    val visibility: Int,
    val cloudiness: Int,
    val sunrise: Long,
    val sunset: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double
) {
    fun temperatureFormatted(): String = "${temperature.toInt()}°"
    fun feelsLikeFormatted(): String = "${feelsLike.toInt()}°"
    fun tempMinFormatted(): String = "${tempMin.toInt()}°"
    fun tempMaxFormatted(): String = "${tempMax.toInt()}°"
    fun windSpeedFormatted(): String = "${windSpeed} m/s"
    fun humidityFormatted(): String = "${humidity}%"
    fun pressureFormatted(): String = "${pressure} hPa"
    fun visibilityFormatted(): String = "${visibility} km"
    fun cloudinessFormatted(): String = "${cloudiness}%"

    fun getWeatherEmoji(): String = when (weatherMain.lowercase()) {
        "thunderstorm" -> "⛈️"
        "drizzle" -> "🌦️"
        "rain" -> "🌧️"
        "snow" -> "❄️"
        "mist", "fog", "haze", "smoke", "dust", "sand", "ash" -> "🌫️"
        "squall", "tornado" -> "🌪️"
        "clear" -> "☀️"
        "clouds" -> if (cloudiness < 50) "⛅" else "☁️"
        else -> "🌡️"
    }

    fun getWindDirection(): String {
        val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        return directions[((windDegree + 22.5) / 45).toInt() % 8]
    }
}