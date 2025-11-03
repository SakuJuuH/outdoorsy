package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.Rain
import com.google.gson.annotations.SerializedName

data class RainDto(@SerializedName("3h") val threeHourVolume: Double? = null)

fun RainDto.toDomain(): Rain = Rain(
    threeHourVolume = threeHourVolume
)
