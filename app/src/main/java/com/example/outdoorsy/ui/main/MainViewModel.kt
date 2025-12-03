package com.example.outdoorsy.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.utils.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MainViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {
    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM.code)

    val appTheme: StateFlow<String> = _appTheme.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getAppTheme().collect { theme ->
                _appTheme.value = theme
            }
        }
    }
}
