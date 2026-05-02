package com.example.weatherapp.presentation.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Dark Color Scheme ─────────────────────────────────
val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    secondary = BlueGrey80,
    tertiary = Cyan80,
    background = Color(0xFF1A1A2E),
    surface = Color(0xFF16213E),
)

// ── Light Color Scheme ────────────────────────────────
val LightColorScheme = lightColorScheme(
    primary = Blue40,
    secondary = BlueGrey40,
    tertiary = Cyan40,
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
)

@Composable
fun WeatherAppTheme(
    isDarkMode: Boolean = true,
    content: @Composable () -> Unit
) {
    // ← اختار الـ scheme حسب الـ isDarkMode
    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}