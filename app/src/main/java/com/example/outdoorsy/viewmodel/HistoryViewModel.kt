package com.example.outdoorsy.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.local.datastore.SearchHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HistoryViewModel @Inject constructor(application: Application) : ViewModel() {
    private val repository = SearchHistoryRepository(application)

    val recentSearches = repository.recentSearches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun submitQuery(query: String) {
        viewModelScope.launch {
            repository.addQuery(query)
        }
    }

    fun removeQuery(query: String) {
        viewModelScope.launch { repository.removeQuery(query) }
    }

    fun clearAll() {
        viewModelScope.launch { repository.clear() }
    }
}
