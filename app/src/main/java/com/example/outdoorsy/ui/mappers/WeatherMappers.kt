package com.example.outdoorsy.ui.mappers

import com.example.outdoorsy.domain.model.weather.ForecastItem
import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.ui.model.DailyForecast
import com.example.outdoorsy.ui.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun WeatherResponse.toUiModel(forecastItems: List<ForecastItem>, units: String): WeatherData =
    WeatherData(
        location = this.name,
        temp = this.main.temperature.toInt(),
        condition = this.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            ?: "",
        high = this.main.maxTemperature.toInt(),
        low = this.main.minTemperature.toInt(),
        humidity = this.main.humidity,
        windSpeed = this.wind.speed.toInt(),
        visibility = this.visibility / 1000.0,
        pressure = this.main.pressure / 10.0,
        sunrise = formatTime(this.sys.sunrise),
        sunset = formatTime(this.sys.sunset),
        forecast = forecastItems.toDailyForecasts(),
        icon = this.weather.firstOrNull()?.icon ?: "",
        unit = units
    )

private fun List<ForecastItem>.toDailyForecasts(): List<DailyForecast> = this.groupBy {
    SimpleDateFormat(
        "yyyy-MM-dd",
        Locale.getDefault()
    ).format(Date(it.timeOfData.toLong() * 1000))
}.values.take(5).map { dayItems ->
    val temps = dayItems.map { it.main.maxTemperature }
    DailyForecast(
        day = SimpleDateFormat(
            "EEE",
            Locale.getDefault()
        ).format(Date(dayItems.first().timeOfData.toLong() * 1000)),
        high = temps.maxOrNull()?.toInt() ?: 0,
        low = temps.minOrNull()?.toInt() ?: 0,
        condition =
        dayItems.first().weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
            ?: "Unknown",
        icon = dayItems.first().weather.firstOrNull()?.icon ?: ""
    )
}

private fun formatTime(timeStamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
    return sdf.format(Date(timeStamp * 1000))
}
