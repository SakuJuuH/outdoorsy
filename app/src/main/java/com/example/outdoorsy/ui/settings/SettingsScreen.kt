package com.example.outdoorsy.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.settings.components.SettingsItem
import com.example.outdoorsy.ui.settings.components.SettingsItemWithSwitch
import com.example.outdoorsy.ui.settings.components.SingleChoiceDialog
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.LocaleHelper
import com.example.outdoorsy.utils.TemperatureSystem

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Title Area
        ScreenTitle(title = stringResource(R.string.settings_screen_title))

        // --- General Section ---
        SectionTitle(
            title = stringResource(id = R.string.settings_screen_section_header_general),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        SettingsItem(
            icon = Icons.Default.Language,
            title = stringResource(id = R.string.settings_screen_language_title),
            subtitle = LocaleHelper.getLanguageName(uiState.language),
            iconContentDescription = stringResource(id = R.string.settings_screen_language_icon),
            onClick = { showLanguageDialog = true }
        )

        SettingsItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = stringResource(id = R.string.settings_screen_dark_mode_title),
            subtitle = stringResource(id = R.string.settings_screen_dark_mode_sub_title),
            checked = uiState.isDarkMode,
            onCheckedChange = { viewModel.setIsDarkMode(it) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // --- Units Section ---
        SectionTitle(
            title = stringResource(id = R.string.settings_screen_section_header_units),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        SettingsItem(
            icon = Icons.Default.AttachMoney,
            title = stringResource(id = R.string.settings_screen_currency),
            subtitle = Currencies.fromCode(uiState.currency).displayName,
            iconContentDescription = stringResource(id = R.string.settings_screen_currency_icon),
            onClick = { showCurrencyDialog = true }
        )

        // Temperature Unit
        SettingsItem(
            icon = Icons.Default.Thermostat,
            title = stringResource(id = R.string.settings_screen_unit_item_title),
            subtitle = TemperatureSystem.fromCode(uiState.temperatureUnit).displayName,
            iconContentDescription = stringResource(id = R.string.settings_screen_unit_icon),
            onClick = { showUnitDialog = true }
        )
    }

    if (showLanguageDialog) {
        SingleChoiceDialog(
            title = stringResource(id = R.string.settings_screen_language_dialog_title),
            options = AppLanguage.entries,
            initialSelection = AppLanguage.entries.find { it.code == uiState.language },
            onConfirm = { language ->
                viewModel.setLanguage(language.code)
                showLanguageDialog = false
            },
            onDismissRequest = { showLanguageDialog = false },
            labelSelector = { LocaleHelper.getLanguageName(it.code) }
        )
    }

    if (showUnitDialog) {
        SingleChoiceDialog(
            title = stringResource(id = R.string.settings_screen_unit_dialog_title),
            options = TemperatureSystem.entries,
            initialSelection = TemperatureSystem.entries.find {
                it.code == uiState.temperatureUnit
            },
            onConfirm = { system ->
                viewModel.setTemperatureUnit(system.code)
                showUnitDialog = false
            },
            onDismissRequest = { showUnitDialog = false },
            labelSelector = { it.displayName }
        )
    }

    if (showCurrencyDialog) {
        SingleChoiceDialog(
            title = stringResource(id = R.string.settings_screen_select_currency),
            options = Currencies.entries,
            initialSelection = Currencies.entries.find { it.code == uiState.currency },
            onConfirm = { currency ->
                viewModel.setCurrency(currency.code)
                showCurrencyDialog = false
            },
            onDismissRequest = { showCurrencyDialog = false },
            labelSelector = { it.displayName }
        )
    }
}
