// presentation/WeatherUiState.kt
package com.example.weatherapp.presentation

import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.Weather

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: Weather? = null,
    val error: String? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val lastSearchedCity: String = "London",
  // ← أضف هذا
    val forecasts: List<DailyForecast> = emptyList(),
    val isForecastLoading: Boolean = false

)