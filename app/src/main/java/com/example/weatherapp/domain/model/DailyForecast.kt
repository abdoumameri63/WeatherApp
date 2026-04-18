package com.example.weatherapp.domain.model

data class DailyForecast(
    val date: String,           // مثل "Mon", "Tue"
    val tempMin: Double,
    val tempMax: Double,
    val weatherMain: String,    // مثل "Rain", "Clear"
    val weatherDescription: String,
    val humidity: Int,
    val windSpeed: Double
) {
    fun tempMinFormatted(): String = "${tempMin.toInt()}°"
    fun tempMaxFormatted(): String = "${tempMax.toInt()}°"

    fun getWeatherEmoji(): String = when (weatherMain.lowercase()) {
        "thunderstorm" -> "⛈️"
        "drizzle", "rain" -> "🌧️"
        "snow" -> "❄️"
        "clear" -> "☀️"
        "clouds" -> "☁️"
        else -> "🌡️"
    }
}