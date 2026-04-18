package com.example.weatherapp.presentation.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.presentation.WeatherViewModel
import com.example.weatherapp.presentation.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastDetailScreen(
    forecastIndex: Int,
    onBackClick: () -> Unit,
    // نستخدم نفس الـ ViewModel لأن البيانات موجودة فيه مسبقاً
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // نأخذ بيانات اليوم المطلوب من القائمة
    val forecast = uiState.forecasts.getOrNull(forecastIndex)

    // نختار الـ gradient حسب نوع الطقس مثل الشاشة الرئيسية
    val backgroundGradient = when (forecast?.weatherMain?.lowercase()) {
        "clear" -> GradientClear
        "clouds" -> GradientCloudy
        "rain", "drizzle" -> GradientRain
        "snow" -> GradientSnow
        "thunderstorm" -> GradientThunder
        "mist", "fog", "haze" -> GradientMist
        else -> GradientDefault
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(backgroundGradient))
    ) {
        if (forecast == null) {
            // لو ما لقينا البيانات
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No data available",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── زر الرجوع ──────────────────────────────────
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── المحتوى ─────────────────────────────────────
                ForecastDetailContent(
                    forecast = forecast,
                    cityName = uiState.weather?.cityName ?: "",
                    country = uiState.weather?.country ?: ""
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ForecastDetailContent(
    forecast: DailyForecast,
    cityName: String,
    country: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // اسم المدينة
        Text(
            text = "$cityName, $country",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        // اليوم
        Text(
            text = forecast.date,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // الإيموجي الكبير
        Text(
            text = forecast.getWeatherEmoji(),
            fontSize = 90.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // أعلى درجة كبيرة
        Text(
            text = forecast.tempMaxFormatted(),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        // الوصف
        Text(
            text = forecast.weatherDescription.replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // H / L
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            TempChip(label = "H", value = forecast.tempMaxFormatted())
            TempChip(label = "L", value = forecast.tempMinFormatted())
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── كروت التفاصيل ────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ForecastInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.WaterDrop,
                label = "Humidity",
                value = "${forecast.humidity}%"
            )
            ForecastInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Air,
                label = "Wind",
                value = "${forecast.windSpeed} m/s"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ForecastInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Thermostat,
                label = "Min Temp",
                value = forecast.tempMinFormatted()
            )
            ForecastInfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Thermostat,
                label = "Max Temp",
                value = forecast.tempMaxFormatted()
            )
        }
    }
}

// نفس شكل InfoCard في الشاشة الرئيسية
@Composable
private fun ForecastInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White.copy(alpha = 0.70f),
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.60f),
                fontSize = 11.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// نفس الـ TempChip من WeatherScreen
@Composable
private fun TempChip(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.20f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.30f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.70f),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}