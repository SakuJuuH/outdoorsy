package com.example.outdoorsy.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.TemperatureSystem
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
        val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")
    }

    fun getLanguage(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.LANGUAGE] ?: AppLanguage.ENGLISH.code
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
        preferences[PreferenceKeys.TEMPERATURE_UNIT] ?: TemperatureSystem.METRIC.code
    }

    suspend fun saveTemperatureUnit(unit: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.TEMPERATURE_UNIT] = unit
        }
    }

    fun getRecentSearches(): Flow<List<String>> = dataStore.data.map { preferences ->
        preferences[PreferenceKeys.RECENT_SEARCHES]?.toList() ?: emptyList()
    }

    suspend fun addRecentSearch(location: String) {
        dataStore.edit { preferences ->
            val currentSearches =
                preferences[PreferenceKeys.RECENT_SEARCHES]?.toMutableList() ?: mutableListOf()

            val normalizedLocation = location.trim().replaceFirstChar { it.uppercase() }

            currentSearches.remove(normalizedLocation)
            currentSearches.add(0, normalizedLocation)

            preferences[PreferenceKeys.RECENT_SEARCHES] = currentSearches.take(5).toSet()
        }
    }
}
