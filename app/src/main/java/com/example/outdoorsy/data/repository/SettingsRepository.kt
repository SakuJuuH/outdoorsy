package com.example.outdoorsy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SettingsRepository @Inject constructor(private val dataStore: DataStore<Preferences>) {

    private object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
    }

    fun getLanguage(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LANGUAGE] ?: "en"
    }

    suspend fun saveLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    fun getDarkMode(): Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.DARK_MODE] ?: false
    }

    suspend fun saveDarkMode(darkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.DARK_MODE] = darkMode
        }
    }

    fun getTemperatureUnit(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.TEMPERATURE_UNIT] ?: "metric"
    }

    suspend fun saveTemperatureUnit(unit: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEMPERATURE_UNIT] = unit
        }
    }
}
