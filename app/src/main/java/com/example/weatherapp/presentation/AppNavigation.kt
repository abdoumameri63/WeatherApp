package com.example.weatherapp // Ensure package is correct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.presentation.WeatherViewModel
import com.example.weatherapp.presentation.ui.ForecastDetailScreen
import com.example.weatherapp.presentation.ui.WeatherScreen
import com.example.weatherapp.presentation.ui.theme.WeatherAppTheme

// Define your Screen routes clearly
sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object ForecastDetail : Screen("detail/{index}") {
        fun createRoute(index: Int) = "detail/$index"
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val rootViewModel: WeatherViewModel = hiltViewModel()
    val uiState by rootViewModel.uiState.collectAsStateWithLifecycle()

    // Now uiState.isDarkMode will be recognized
    WeatherAppTheme( uiState.isDarkMode) {
        NavHost(
            navController = navController,
            startDestination = Screen.Weather.route
        ) {
            composable(Screen.Weather.route) {
                WeatherScreen(
                    onForecastClick = { index ->
                        navController.navigate(Screen.ForecastDetail.createRoute(index))
                    }
                )
            }
            composable(
                route = Screen.ForecastDetail.route,
                arguments = listOf(navArgument("index") { type = NavType.IntType })
            ) { backStackEntry ->
                val index = backStackEntry.arguments?.getInt("index") ?: 0
                val parentEntry = remember(backStackEntry) {
                    try {
                        navController.getBackStackEntry(Screen.Weather.route)
                    } catch (e: Exception) {
                        null
                    }
                }
                // Use the ViewModel scoped to the NavGraph or the WeatherScreen
                val viewModel: WeatherViewModel = hiltViewModel(
                    remember(backStackEntry) { navController.getBackStackEntry(Screen.Weather.route) }
                )

                ForecastDetailScreen(
                    forecastIndex = index,
                    onBackClick = { navController.popBackStack() },
                    viewModel = viewModel
                )
            }
        }
    }
}