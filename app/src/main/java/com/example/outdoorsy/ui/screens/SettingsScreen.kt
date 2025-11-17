package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.theme.spacing
import com.example.outdoorsy.utils.LocaleHelper
import com.example.outdoorsy.utils.TemperatureSystem
import com.example.outdoorsy.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, viewModel: SettingsViewModel = hiltViewModel()) {
    val language = viewModel.language.collectAsState().value
    val unit = viewModel.temperatureUnit.collectAsState().value
    val isDarkMode = viewModel.isDarkMode.collectAsState().value

    var selectedLanguage by remember { mutableStateOf(language) }
    var selectedUnit by remember { mutableStateOf(unit) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showUnitDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing(4), vertical = MaterialTheme.spacing(3))
        ) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(2)))
            Text(
                text = stringResource(id = R.string.settings_screen_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))
        }

        SettingsSectionHeader(
            title = stringResource(id = R.string.settings_screen_section_header_general)
        )

        SettingsItem(
            icon = Icons.Default.Language,
            title = stringResource(id = R.string.settings_screen_language_title),
            subtitle =
                LocaleHelper.supportedLanguages[selectedLanguage]?.displayLanguage?.replaceFirstChar(
                    Char::uppercase
                ) ?: "",
            onClick = {
                selectedLanguage = language
                showLanguageDialog = true
            }
        )

        SettingsItemWithSwitch(
            icon = Icons.Default.DarkMode,
            title = stringResource(id = R.string.settings_screen_dark_mode_title),
            subtitle = stringResource(id = R.string.settings_screen_dark_mode_sub_title),
            checked = isDarkMode,
            onCheckedChange = { viewModel.setIsDarkMode(it) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsSectionHeader(
            title = stringResource(id = R.string.settings_screen_section_header_units)
        )

        SettingsItem(
            icon = Icons.Default.Thermostat,
            title = stringResource(id = R.string.settings_screen_unit_item_title),
            subtitle = TemperatureSystem.DISPLAY_NAMES[selectedUnit] ?: "",
            onClick = {
                selectedUnit = unit
                showUnitDialog = true
            }
        )
    }

    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = {
                selectedLanguage = language
                showLanguageDialog = false
            },
            title = { Text(stringResource(id = R.string.settings_screen_language_dialog_title)) },
            text = {
                Column {
                    LocaleHelper.supportedLanguages.forEach { (code, locale) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedLanguage = code
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = code == selectedLanguage,
                                onClick = {
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                locale.displayLanguage.replaceFirstChar(Char::uppercase)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setLanguage(selectedLanguage)
                    showLanguageDialog = false
                }) {
                    Text(stringResource(id = R.string.settings_screen_dialog_confirm_button))
                }
            }
        )
    }

    if (showUnitDialog) {
        AlertDialog(
            onDismissRequest = {
                selectedUnit = unit
                showUnitDialog = false
            },
            title = { Text(stringResource(id = R.string.settings_screen_unit_dialog_title)) },
            text = {
                Column {
                    TemperatureSystem.DISPLAY_NAMES.forEach { (unit, name) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedUnit = unit }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = unit == selectedUnit,
                                onClick = {}
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(name)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setTemperatureUnit(selectedUnit)
                    showUnitDialog = false
                }) {
                    Text(stringResource(id = R.string.settings_screen_dialog_confirm_button))
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsItemWithSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}