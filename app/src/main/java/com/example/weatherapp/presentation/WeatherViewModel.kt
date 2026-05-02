// presentation/WeatherViewModel.kt
package com.example.weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.location.LocationTracker
import com.example.weatherapp.domain.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository,
    private val locationTracker: LocationTracker  // ← أضف هذا

) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    init {
        fetchWeather("Alger")
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onSearchActiveChange(active: Boolean) {
        _uiState.update { it.copy(isSearchActive = active) }
    }

    fun onSearchSubmit() {
        val city = _uiState.value.searchQuery
        if (city.isBlank()) return
        _uiState.update { it.copy(isSearchActive = false) }
        fetchWeather(city)
    }

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    lastSearchedCity = city,
                    searchQuery = ""
                )
            }

            val weatherJob = launch {
                repository.getWeatherByCity(city).fold(
                    onSuccess = { weather ->
                        _uiState.update { it.copy(weather = weather) }
                    },
                    onFailure = { error ->
                        _uiState.update { it.copy(error = error.message) }
                    }
                )
            }
            val forecastJob = launch {
                _uiState.update { it.copy(isForecastLoading = true) }
                repository.getForecastByCity(city).fold(
                    onSuccess = { forecasts ->
                        _uiState.update { it.copy(forecasts = forecasts) }
                    },
                    onFailure = {
                        // لو فشل الـ forecast ما نوقف كل شيء
                        _uiState.update { it.copy(forecasts = emptyList()) }
                    }
                )
                _uiState.update { it.copy(isForecastLoading = false) }
            }

            // ننتظر الاثنين يخلصون
            weatherJob.join()
            forecastJob.join()

            _uiState.update { it.copy(isLoading = false) }
        }
    }
    fun retry() {
        fetchWeather(_uiState.value.lastSearchedCity)
    }
    fun toggleDarkMode() {
        _uiState.update { it.copy(isDarkMode = !it.isDarkMode) }
    }
    fun fetchWeatherByLocation() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, error = null)
            }

            // نجلب اسم المدينة من الـ GPS
            val cityName = locationTracker.getCurrentCityName()

            if (cityName != null) {
                // لو حصلنا على المدينة، نجلب طقسها
                fetchWeather(cityName)
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Could not get your location. Please search manually."
                    )
                }
            }
        }
    }


}


