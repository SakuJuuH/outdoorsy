package com.example.outdoorsy.domain.model

import com.example.outdoorsy.data.local.entity.ActivityEntity

data class Activity(
    val name: String
) {
    fun toEntity(): ActivityEntity = ActivityEntity(name = name)
}