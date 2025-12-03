package com.example.outdoorsy.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_rates")
data class CurrencyRateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val baseCurrency: String,
    val targetCurrency: String,
    val rate: Double,
    val timestamp: Long
)