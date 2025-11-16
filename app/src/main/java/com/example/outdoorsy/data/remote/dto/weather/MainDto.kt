package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.Main
import com.google.gson.annotations.SerializedName

data class MainDto(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    @SerializedName("temp_min")
    val tempMin: Double?,
    @SerializedName("temp_max")
    val tempMax: Double?,
    val pressure: Int,
    val humidity: Int,
    @SerializedName("sea_level")
    val seaLevel: Int,
    @SerializedName("grnd_level")
    val grndLevel: Int
)

fun MainDto.toDomain() = Main(
    temperature = temp,
    feelsLikeTemperature = feelsLike,
    minTemperature = tempMin ?: 0.0,
    maxTemperature = tempMax ?: 0.0,
    pressure = pressure,
    humidity = humidity,
    seaLevel = seaLevel,
    groundLevel = grndLevel
)
