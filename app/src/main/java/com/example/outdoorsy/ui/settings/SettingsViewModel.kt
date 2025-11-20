package com.example.outdoorsy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.utils.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    init {
        loadSettings()
    }

    private val _temperatureUnit = MutableStateFlow("")
    private val _language = MutableStateFlow("")
    private val _isDarkMode = MutableStateFlow(false)

    val temperatureUnit = _temperatureUnit.asStateFlow()
    val language = _language.asStateFlow()
    val isDarkMode = _isDarkMode.asStateFlow()

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

    fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getTemperatureUnit().collect { unit ->
                _temperatureUnit.value = unit
            }
        }
        viewModelScope.launch {
            settingsRepository.getDarkMode().collect { isDarkMode ->
                _isDarkMode.value = isDarkMode
            }
        }
        viewModelScope.launch {
            settingsRepository.getLanguage().collect { language ->
                _language.value = language
            }
        }
    }
}
