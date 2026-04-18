package com.example.weatherapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weatherapp.presentation.WeatherViewModel
import com.example.weatherapp.presentation.ui.ForecastDetailScreen
import com.example.weatherapp.presentation.ui.WeatherScreen

// اسماء الشاشات
sealed class Screen(val route: String) {
    object Weather : Screen("weather")
    object ForecastDetail : Screen("forecast_detail/{index}") {
        // دالة مساعدة تبني الرابط مع الرقم
        fun createRoute(index: Int) = "forecast_detail/$index"
    }
}

@Composable
fun AppNavigation() {
    // هذا الـ controller هو اللي يتحكم في التنقل بين الشاشات
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Weather.route  // الشاشة الأولى عند فتح التطبيق
    ) {
        // الشاشة الرئيسية
        composable(route = Screen.Weather.route) {
            WeatherScreen(
                onForecastClick = { index ->
                    // لما يضغط على يوم، ننتقل لشاشة التفاصيل ونمرر رقم اليوم
                    navController.navigate(Screen.ForecastDetail.createRoute(index))
                }
            )
        }

        // شاشة تفاصيل اليوم — تستقبل رقم اليوم
        composable(
            route = Screen.ForecastDetail.route,
            arguments = listOf(
                navArgument("index") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            // نأخذ رقم اليوم من الـ route
            val index = backStackEntry.arguments?.getInt("index") ?: 0
            val weatherEntry = remember(backStackEntry) {
                navController.getBackStackEntry(Screen.Weather.route)
            }
            val viewModel: WeatherViewModel = hiltViewModel(weatherEntry)
            ForecastDetailScreen(
                forecastIndex = index,
                onBackClick = {
                    // زر الرجوع
                    navController.popBackStack()

                },
                    viewModel = viewModel
            )
        }
    }
}