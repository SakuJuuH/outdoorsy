package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.utils.Language
import com.example.outdoorsy.utils.LocaleHelper
import com.example.outdoorsy.utils.TemperatureSystem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    val temperatureUnit = settingsRepository.getTemperatureUnit()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TemperatureSystem.METRIC)

    val isDarkMode = settingsRepository.getDarkMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val language = settingsRepository.getLanguage()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Language.ENGLISH)

    fun setTemperatureUnit(unit: String) {
        viewModelScope.launch {
            settingsRepository.saveTemperatureUnit(unit)
        }
    }

    fun setIsDarkMode(isDarkMode: Boolean) {
        viewModelScope.launch {
            settingsRepository.saveDarkMode(isDarkMode)
        }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsRepository.saveLanguage(language)
            LocaleHelper.setLocale(languageCode = language)
        }
    }
}
