package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class HourlyForecastDto(
    val dt: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Int,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_gust")
    val windGust: Double?,
    @SerializedName("wind_deg")
    val windDeg: Int,
    val pop: Double,
    val rain: PrecipitationDto?,
    val snow: PrecipitationDto?,
    val weather: List<WeatherConditionDto>
)
