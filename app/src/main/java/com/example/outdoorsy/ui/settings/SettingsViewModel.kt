package com.example.outdoorsy.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.utils.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getTemperatureUnit(),
        settingsRepository.getDarkMode(),
        settingsRepository.getLanguage(),
        settingsRepository.getCurrency()
    ) { temperatureUnit, isDarkMode, language, currency ->
        SettingsUiState(
            temperatureUnit = temperatureUnit,
            isDarkMode = isDarkMode,
            language = language,
            currency = currency
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

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

    fun setCurrency(currency: String) {
        viewModelScope.launch {
            settingsRepository.saveCurrency(currency)
        }
    }
}
