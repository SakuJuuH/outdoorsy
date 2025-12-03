package com.example.outdoorsy.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    private val _isDarkMode = MutableStateFlow(false)

    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.isDarkMode.collect { isDarkMode ->
                _isDarkMode.value = isDarkMode
            }
        }
    }
}
