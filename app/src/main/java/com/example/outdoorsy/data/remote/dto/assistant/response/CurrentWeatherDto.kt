package com.example.outdoorsy.data.remote.dto.assistant.response

import com.google.gson.annotations.SerializedName

data class CurrentWeatherDto(
    val dt: Long,
    val sunrise: Long?,
    val sunset: Long?,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val clouds: Int,
    val uvi: Double,
    val visibility: Int,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_gust")
    val windGust: Double?,
    @SerializedName("wind_deg")
    val windDeg: Int,
    val rain: PrecipitationDto?,
    val snow: PrecipitationDto?,
    val weather: List<WeatherConditionDto>
)
