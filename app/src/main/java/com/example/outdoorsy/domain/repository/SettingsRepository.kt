package com.example.outdoorsy.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLanguage(): Flow<String>
    suspend fun saveLanguage(language: String)

    fun getTemperatureUnit(): Flow<String>
    suspend fun saveTemperatureUnit(unit: String)

    fun getCurrency(): Flow<String>
    suspend fun saveCurrency(currency: String)

    fun getAppTheme(): Flow<String>
    suspend fun saveAppTheme(theme: String)

    fun getRecentSearches(): Flow<List<String>>
    suspend fun addRecentSearch(location: String)
}
