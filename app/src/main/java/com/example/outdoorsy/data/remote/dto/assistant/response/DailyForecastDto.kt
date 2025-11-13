package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class DailyForecastDto(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val moonrise: Long?,
    val moonset: Long?,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val summary: String?,
    val temp: DailyTemperatureDto,
    @SerializedName("feels_like")
    val feelsLike: DailyTemperatureDto,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_gust")
    val windGust: Double?,
    @SerializedName("wind_deg")
    val windDeg: Int,
    val clouds: Int,
    val uvi: Double,
    val pop: Double,
    val rain: Double?,
    val snow: Double?,
    val weather: List<WeatherConditionDto>
)
