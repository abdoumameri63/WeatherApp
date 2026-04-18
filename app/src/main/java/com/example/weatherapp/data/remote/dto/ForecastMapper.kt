package com.example.weatherapp.data.remote.dto

import com.example.weatherapp.domain.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

fun ForecastResponseDto.toDailyForecasts(): List<DailyForecast> {
    // نجمع كل الـ items حسب اليوم
    val grouped = list.groupBy { item ->
        // نأخذ فقط التاريخ من "2024-01-15 12:00:00" → "2024-01-15"
        item.dateText.substring(0, 10)
    }

    val dayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // Mon, Tue...

    return grouped
        .entries
        .drop(1)          // نحذف اليوم الحالي لأن عندنا الـ current weather
        .take(5)          // نأخذ 5 أيام فقط
        .map { (dateStr, items) ->
            // نحسب أعلى وأدنى درجة لهذا اليوم من كل الـ items
            val minTemp = items.minOf { it.main.tempMin }
            val maxTemp = items.maxOf { it.main.tempMax }

            // نأخذ بيانات الطقس من الـ item اللي في منتصف النهار (أو الأول)
            val middayItem = items.firstOrNull {
                it.dateText.contains("12:00:00")
            } ?: items.first()

            // نحول التاريخ من "2024-01-15" إلى "Mon"
            val date = dayFormat.parse(dateStr)
            val dayName = date?.let { displayFormat.format(it) } ?: dateStr

            DailyForecast(
                date = dayName,
                tempMin = minTemp,
                tempMax = maxTemp,
                weatherMain = middayItem.weather.firstOrNull()?.main ?: "",
                weatherDescription = middayItem.weather.firstOrNull()?.description ?: "",
                humidity = middayItem.main.humidity,
                windSpeed = middayItem.wind.speed
            )
        }
}