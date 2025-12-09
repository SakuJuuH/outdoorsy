package com.example.outdoorsy.ui.weather.mappers

import com.example.outdoorsy.domain.model.weather.WeatherResponse
import com.example.outdoorsy.domain.model.weather.components.ForecastItem
import com.example.outdoorsy.ui.weather.model.DailyForecast
import com.example.outdoorsy.ui.weather.model.WeatherData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.SimpleTimeZone
import java.util.TimeZone

fun WeatherResponse.toUiModel(forecastItems: List<ForecastItem>, units: String): WeatherData {
    val locationTimeZone = SimpleTimeZone(this.timezone * 1000, "LocationTime")

    return WeatherData(
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
        sunrise = formatTime(this.sys.sunrise, locationTimeZone),
        sunset = formatTime(this.sys.sunset, locationTimeZone),
        forecast = forecastItems.toDailyForecasts(locationTimeZone),
        icon = this.weather.firstOrNull()?.icon ?: "",
        unit = units
    )
}

private fun List<ForecastItem>.toDailyForecasts(timeZone: TimeZone): List<DailyForecast> {
    val dayFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        this.timeZone = timeZone
    }
    val dayNameFormatter = SimpleDateFormat("EEE", Locale.getDefault()).apply {
        this.timeZone = timeZone
    }
    return this.groupBy {
        dayFormatter.format(Date(it.timeOfData.toLong() * 1000))
    }.values.take(5).map { dayItems ->
        val temps = dayItems.map { it.main.maxTemperature }
        DailyForecast(
            day = dayNameFormatter.format(Date(dayItems.first().timeOfData.toLong() * 1000)),
            high = temps.maxOrNull()?.toInt() ?: 0,
            low = temps.minOrNull()?.toInt() ?: 0,
            condition =
                dayItems.first().weather.firstOrNull()?.description?.replaceFirstChar {
                    it.uppercase()
                }
                    ?: "Unknown",
            icon = dayItems.first().weather.firstOrNull()?.icon ?: ""
        )
    }
}

private fun formatTime(timeStamp: Long, timeZone: TimeZone): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.timeZone = timeZone
    return sdf.format(Date(timeStamp * 1000))
}
