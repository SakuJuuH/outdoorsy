package com.example.outdoorsy.data.remote.dto

import com.example.outdoorsy.domain.model.ForecastSys
import com.example.outdoorsy.domain.model.PartOfDay

data class ForecastSysDto(val pod: String)

fun ForecastSysDto.toDomain(): ForecastSys = ForecastSys(
    pod = PartOfDay.fromCode(pod)
)
