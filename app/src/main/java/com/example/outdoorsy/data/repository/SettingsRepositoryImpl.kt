package com.example.outdoorsy.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.example.outdoorsy.domain.repository.SettingsRepository
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.TemperatureSystem
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Singleton
class SettingsRepositoryImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    SettingsRepository {

    private object PreferenceKeys {
        val LANGUAGE = stringPreferencesKey("language")
        val APP_THEME = stringPreferencesKey("app_theme")
        val TEMPERATURE_UNIT = stringPreferencesKey("temperature_unit")
        val RECENT_SEARCHES = stringSetPreferencesKey("recent_searches")
        val CURRENCY_CODE = stringPreferencesKey("currency_code")
    }

    private fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
        dataStore.data.catch { exception ->
            if (exception is IOException) {
                Log.e("SettingsRepository", "Error reading preferences", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }

    private suspend fun <T> setPreference(key: Preferences.Key<T>, value: T) {
        try {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        } catch (e: IOException) {
            Log.e("SettingsRepository", "Error writing preferences", e)
            throw e
        }
    }

    override fun getLanguage(): Flow<String> =
        getPreference(PreferenceKeys.LANGUAGE, AppLanguage.ENGLISH.code)

    override suspend fun saveLanguage(language: String) =
        setPreference(PreferenceKeys.LANGUAGE, language)

    override fun getTemperatureUnit(): Flow<String> = getPreference(
        PreferenceKeys.TEMPERATURE_UNIT,
        TemperatureSystem.METRIC.code
    )

    override suspend fun saveTemperatureUnit(unit: String) = setPreference(
        PreferenceKeys.TEMPERATURE_UNIT,
        unit
    )

    override fun getRecentSearches(): Flow<List<String>> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[PreferenceKeys.RECENT_SEARCHES]?.toList() ?: emptyList()
        }

    override suspend fun addRecentSearch(location: String) {
        try {
            dataStore.edit { preferences ->
                val currentSearches =
                    preferences[PreferenceKeys.RECENT_SEARCHES]?.toMutableList() ?: mutableListOf()

                val normalizedLocation = location.trim().replaceFirstChar { it.uppercase() }

                currentSearches.remove(normalizedLocation)
                currentSearches.add(0, normalizedLocation)

                preferences[PreferenceKeys.RECENT_SEARCHES] = currentSearches.take(5).toSet()
            }
        } catch (e: IOException) {
            Log.e("SettingsRepo", "Error adding recent search", e)
        }
    }

    override suspend fun saveCurrency(currency: String) =
        setPreference(PreferenceKeys.CURRENCY_CODE, currency)

    override fun getCurrency(): Flow<String> = getPreference(
        PreferenceKeys.CURRENCY_CODE,
        Currencies.GBP.code
    )

    override suspend fun saveAppTheme(theme: String) =
        setPreference(PreferenceKeys.APP_THEME, theme)

    override fun getAppTheme(): Flow<String> = getPreference(
        PreferenceKeys.APP_THEME,
        AppTheme.SYSTEM.code
    )
}
