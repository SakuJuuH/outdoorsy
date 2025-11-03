package com.example.outdoorsy.domain.model

enum class PartOfDay(val code: String) {
    DAY("d"),
    NIGHT("n");

    companion object {
        fun fromCode(code: String) = entries.find { it.code == code } ?: DAY
    }
}
