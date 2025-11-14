package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class LocationWeatherDataDto(
    val current: String?,
    @SerializedName("current Week Day UTC")
    val currentWeekDayUtc: String?,
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Int,
    val currentWeather: CurrentWeatherDto?,
    val minutely: List<MinutelyForecastDto>?,
    val hourly: List<HourlyForecastDto>?,
    val daily: List<DailyForecastDto>?,
    val alerts: List<WeatherAlertDto>?
)
