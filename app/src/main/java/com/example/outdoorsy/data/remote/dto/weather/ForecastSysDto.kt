package com.example.outdoorsy.data.remote.dto.weather

import com.example.outdoorsy.domain.model.weather.ForecastSys
import com.example.outdoorsy.domain.model.weather.PartOfDay

data class ForecastSysDto(val pod: String)

fun ForecastSysDto.toDomain(): ForecastSys = ForecastSys(
    pod = PartOfDay.fromCode(pod)
)
