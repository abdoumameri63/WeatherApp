// presentation/ui/WeatherScreen.kt
package com.example.weatherapp.presentation.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
// أضف هذين الـ import في أعلى الملف
import androidx.compose.material.icons.outlined.MyLocation
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.domain.model.DailyForecast
import com.example.weatherapp.domain.model.Weather
import com.example.weatherapp.presentation.WeatherViewModel
import com.example.weatherapp.presentation.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    onForecastClick: (Int) -> Unit,
    viewModel: WeatherViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val backgroundGradient by remember(uiState.weather?.weatherMain) {
        derivedStateOf {
            when (uiState.weather?.weatherMain?.lowercase()) {
                "clear" -> GradientClear
                "clouds" -> GradientCloudy
                "rain", "drizzle" -> GradientRain
                "snow" -> GradientSnow
                "thunderstorm" -> GradientThunder
                "mist", "fog", "haze", "smoke" -> GradientMist
                else -> GradientDefault
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(backgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Search Bar ──────────────────────────────────────────────

            LocationAwareSearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                onSearch = {
                    viewModel.onSearchSubmit()
                    keyboardController?.hide()
                    focusManager.clearFocus()
                },
                onLocationClick = { viewModel.fetchWeatherByLocation() }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // ── Main Content ─────────────────────────────────────────────
            AnimatedContent(
                targetState = uiState.isLoading,
                transitionSpec = {
                    fadeIn(animationSpec = tween(400)) togetherWith
                            fadeOut(animationSpec = tween(400))
                },
                label = "main_content"
            ) { loading ->
                if (loading) {
                    LoadingSection()
                } else if (uiState.error != null) {
                    ErrorSection(
                        message = uiState.error!!,
                        onRetry = viewModel::retry
                    )
                } else if (uiState.weather != null) {
                    WeatherContent(weather = uiState.weather!!,forecasts = uiState.forecasts,
                        isForecastLoading = uiState.isForecastLoading,
                        onForecastClick = onForecastClick  // ← أضف هذا
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}



@Composable
private fun WeatherContent(weather: Weather,
                           forecasts: List<DailyForecast>,
                           isForecastLoading: Boolean,
                           onForecastClick: (Int) -> Unit  // ← أضف هذا
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // City name + country
        Text(
            text = "${weather.cityName}, ${weather.country}",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold
        )

        // Date
        val dateStr = remember(weather.timestamp) {
            SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())
                .format(Date(weather.timestamp * 1000))
        }
        Text(
            text = dateStr,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.75f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Weather Emoji Icon
        Text(
            text = weather.getWeatherEmoji(),
            fontSize = 90.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Big Temperature
        Text(
            text = weather.temperatureFormatted(),
            style = MaterialTheme.typography.displayLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )

        // Description
        Text(
            text = weather.weatherDescription,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White.copy(alpha = 0.85f)
        )

        Spacer(modifier = Modifier.height(12.dp))


        // High / Low
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            TempChip(label = "H", value = weather.tempMaxFormatted())
            TempChip(label = "L", value = weather.tempMinFormatted())
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── Info Cards Grid ───────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.WaterDrop,
                label = "Humidity",
                value = weather.humidityFormatted()
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Air,
                label = "Wind",
                value = "${weather.windSpeedFormatted()} ${weather.getWindDirection()}"
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Thermostat,
                label = "Feels Like",
                value = weather.feelsLikeFormatted()
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Speed,
                label = "Pressure",
                value = weather.pressureFormatted()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Visibility,
                label = "Visibility",
                value = weather.visibilityFormatted()
            )
            InfoCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Outlined.Cloud,
                label = "Cloudiness",
                value = weather.cloudinessFormatted()
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Sunrise / Sunset ─────────────────────────────────────────
        SunriseSunsetCard(
            sunrise = weather.sunrise,
            sunset = weather.sunset
        )
        Spacer(modifier = Modifier.height(12.dp))

        ForecastSection(
            forecasts = forecasts,
            isLoading = isForecastLoading,
            onForecastClick = onForecastClick
        )
    }
}

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

@Composable
private fun InfoCard(
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

@Composable
private fun SunriseSunsetCard(
    sunrise: Long,
    sunset: Long
) {
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val sunriseStr = timeFormat.format(Date(sunrise * 1000))
    val sunsetStr = timeFormat.format(Date(sunset * 1000))

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = CardBackground,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SunTimeItem(emoji = "🌅", label = "Sunrise", time = sunriseStr)
            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color.White.copy(alpha = 0.30f)
            )
            SunTimeItem(emoji = "🌇", label = "Sunset", time = sunsetStr)
        }
    }
}

@Composable
private fun SunTimeItem(emoji: String, label: String, time: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = emoji, fontSize = 28.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 11.sp
        )
        Text(
            text = time,
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LoadingSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Fetching weather...",
                color = Color.White.copy(alpha = 0.80f),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ErrorSection(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "⚠️", fontSize = 52.sp)
            Text(
                text = message,
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            FilledTonalButton(
                onClick = onRetry,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color.White.copy(alpha = 0.25f),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again")
            }
        }
    }
}
@Composable
private fun ForecastSection(
    forecasts: List<DailyForecast>,
    isLoading: Boolean,
    onForecastClick: (Int) -> Unit  // ← أضف هذا

) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "5-Day Forecast",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(28.dp)
                )
            }
        } else {
            // ← نحفظ أي يوم مفتوح الآن
            var expandedIndex by remember { mutableStateOf<Int?>(null) }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = CardBackground,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    forecasts.forEachIndexed { index, forecast ->
                        ForecastRow(
                            forecast = forecast,
                            isExpanded = expandedIndex == index,
                            onClick = {
                                // لو نفس اليوم مضغوط → اطوه، لو غيره → افتحه
                                onForecastClick(index)
                            }
                        )
                        if (index < forecasts.lastIndex) {
                            Divider(color = Color.White.copy(alpha = 0.15f),
                                modifier = Modifier.padding(horizontal = 8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastRow(
    forecast: DailyForecast,
    isExpanded: Boolean,
    onClick: () -> Unit

) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }  // ← كل الصف قابل للضغط
            .padding(horizontal = 12.dp, vertical = 12.dp)
    ) {
        // ── الصف الرئيسي (دائماً ظاهر) ──────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // اليوم
            Text(
                text = forecast.date,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.width(48.dp)
            )

            // الإيموجي
            Text(
                text = forecast.getWeatherEmoji(),
                fontSize = 22.sp,
                modifier = Modifier.weight(1f)
            )

            // الوصف
            Text(
                text = forecast.weatherDescription.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.weight(2f)
            )

            // أدنى درجة
            Text(
                text = forecast.tempMinFormatted(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.60f),
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.width(8.dp))

            // أعلى درجة
            Text(
                text = forecast.tempMaxFormatted(),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.width(8.dp))

            // سهم يتدوّر لما يتوسع
            Icon(
                imageVector = if (isExpanded)
                    Icons.Default.KeyboardArrowUp
                else
                    Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.60f),
                modifier = Modifier.size(20.dp)
            )
        }

        // ── التفاصيل (تظهر فقط لما isExpanded = true) ──
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                Divider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(12.dp))

                // صفين من التفاصيل
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ForecastDetailItem(
                        icon = Icons.Outlined.WaterDrop,
                        label = "Humidity",
                        value = "${forecast.humidity}%"
                    )
                    ForecastDetailItem(
                        icon = Icons.Outlined.Air,
                        label = "Wind",
                        value = "${forecast.windSpeed} m/s"
                    )
                    ForecastDetailItem(
                        icon = Icons.Outlined.Thermostat,
                        label = "Feels Like",
                        value = forecast.tempMinFormatted()
                    )
                }
            }
        }
    }
}

// ── كل عنصر تفصيل صغير ──────────────────────────────
@Composable
private fun ForecastDetailItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White.copy(alpha = 0.70f),
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.60f),
            fontSize = 11.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }

}
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LocationAwareSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onLocationClick: () -> Unit
) {
    // نطلب صلاحية الموقع
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // خانة البحث الموجودة
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text("Search city...", color = Color.White.copy(alpha = 0.6f))
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search",
                    tint = Color.White.copy(alpha = 0.8f))
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear",
                            tint = Color.White.copy(alpha = 0.8f))
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch() }),
            singleLine = true,
            shape = RoundedCornerShape(28.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.White.copy(alpha = 0.8f),
                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                cursorColor = Color.White,
                focusedContainerColor = Color.White.copy(alpha = 0.15f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.10f)
            )
        )

        // ← زر الـ GPS الجديد
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.15f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
            modifier = Modifier.size(56.dp)
        ) {
            IconButton(
                onClick = {
                    if (locationPermissions.allPermissionsGranted) {
                        // الصلاحية موجودة — اجلب الموقع مباشرة
                        onLocationClick()
                    } else {
                        // اطلب الصلاحية من المستخدم
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Outlined.MyLocation,
                    contentDescription = "Use my location",
                    tint = Color.White
                )
            }
        }
    }

    // لو المستخدم منح الصلاحية الآن — اجلب الموقع تلقائياً
    LaunchedEffect(locationPermissions.allPermissionsGranted) {
        if (locationPermissions.allPermissionsGranted) {
            onLocationClick()
        }
    }
}