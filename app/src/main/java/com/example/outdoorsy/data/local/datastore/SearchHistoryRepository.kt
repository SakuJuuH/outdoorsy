package com.example.outdoorsy.data.local.datastore

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Application.searchDataStore by preferencesDataStore(name = "search_prefs")

class SearchHistoryRepository(private val application: Application) {
    private val key = stringPreferencesKey("recent_searches")
    private val separator = "|"
    private val maxItems = 10

    val recentSearches: Flow<List<String>> = application.searchDataStore.data.map { prefs ->
        prefs[key]?.split(separator)?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun addQuery(query: String) {
        val sanitized = query.trim()
        if (sanitized.isEmpty()) return
        application.searchDataStore.edit { prefs ->
            val current = prefs[key]?.split(separator)?.toMutableList() ?: mutableListOf()
            current.removeAll { it.equals(sanitized, ignoreCase = true) }
            current.add(0, sanitized)
            while (current.size > maxItems) current.removeLast()
            prefs[key] = current.joinToString(separator)
        }
    }

    suspend fun removeQuery(query: String) {
        application.searchDataStore.edit { prefs ->
            val current = prefs[key]?.split(separator)?.toMutableList() ?: mutableListOf()
            current.removeAll { it.equals(query, ignoreCase = true) }
            prefs[key] = current.joinToString(separator)
        }
    }

    suspend fun clear() {
        application.searchDataStore.edit { prefs ->
            prefs.remove(key)
        }
    }
}


