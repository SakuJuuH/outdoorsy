package com.example.outdoorsy.utils

import androidx.room.TypeConverter
import java.time.LocalDateTime

class DateTimeConverters{
    @TypeConverter
    fun fromTimestamp(value: String?): LocalDateTime? {
        return value?.let { LocalDateTime.parse(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): String? {
        return date?.toString()
    }
}