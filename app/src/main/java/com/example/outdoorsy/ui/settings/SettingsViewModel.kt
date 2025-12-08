package com.example.outdoorsy.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settingsRepository: SettingsRepository) :
    ViewModel() {

    private val userMessage = MutableStateFlow<Int?>(null)

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.getTemperatureUnit(),
        settingsRepository.getAppTheme(),
        settingsRepository.getLanguage(),
        settingsRepository.getCurrency(),
        userMessage
    ) { temperatureUnit, appTheme, language, currency, message ->
        SettingsUiState(
            temperatureUnit = temperatureUnit,
            appTheme = appTheme,
            language = language,
            currency = currency,
            userMessage = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SettingsUiState()
    )

    fun setTemperatureUnit(unit: String) {
        launchWithHandler {
            settingsRepository.saveTemperatureUnit(unit)
        }
    }

    fun setAppTheme(theme: String) {
        launchWithHandler {
            settingsRepository.saveAppTheme(theme)
        }
    }

    fun setLanguage(language: String) {
        launchWithHandler {
            settingsRepository.saveLanguage(language)
        }
    }

    fun setCurrency(currency: String) {
        launchWithHandler {
            settingsRepository.saveCurrency(currency)
        }
    }

    fun messageShown() {
        userMessage.update { null }
    }

    private fun launchWithHandler(block: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                block()
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error saving settings", e)
                userMessage.value = R.string.generic_error
            }
        }
    }
}
