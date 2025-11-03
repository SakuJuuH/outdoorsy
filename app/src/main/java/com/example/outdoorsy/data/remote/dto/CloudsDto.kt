package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.Clouds

data class CloudsDto(val all: Int)

fun CloudsDto.toDomain(): Clouds = Clouds(
    cloudiness = all
)
