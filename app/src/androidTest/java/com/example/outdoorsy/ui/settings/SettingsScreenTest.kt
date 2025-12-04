package com.example.outdoorsy.ui.settings

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDialog
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.utils.AppLanguage
import com.example.outdoorsy.utils.AppTheme
import com.example.outdoorsy.utils.Currencies
import com.example.outdoorsy.utils.LocaleHelper
import com.example.outdoorsy.utils.TemperatureSystem
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse

import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun `settings screen displays all sections and values correctly`() {
        val sampleState = SettingsUiState(
            language = AppLanguage.ENGLISH.code,
            appTheme = AppTheme.LIGHT.code,
            temperatureUnit = TemperatureSystem.METRIC.code,
            currency = Currencies.EUR.code
        )

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {})
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_section_header_general))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_section_header_units))
            .assertIsDisplayed()

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_language_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(LocaleHelper.getLanguageName(sampleState.language))
            .assertIsDisplayed() // Subtitle

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_app_theme_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(AppTheme.fromCode(sampleState.appTheme).displayName))
            .assertIsDisplayed() // Subtitle

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_currency))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(Currencies.fromCode(sampleState.currency).displayName))
            .assertIsDisplayed() // Subtitle

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_unit_item_title))
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(context.getString(TemperatureSystem.fromCode(sampleState.temperatureUnit).displayName))
            .assertIsDisplayed() // Subtitle
    }

    @Test
    fun `clicking language item shows dialog`() {
        val sampleState = SettingsUiState(language = AppLanguage.ENGLISH.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {})
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_language_title))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_language_dialog_title))
            .assertIsDisplayed()

        for (language in AppLanguage.entries) {
            val name = LocaleHelper.getLanguageName(language.code)

            composeTestRule.onNode(
                hasText(name) and hasAnyAncestor(isDialog())
            ).assertIsDisplayed()
        }
    }

    @Test
    fun `clicking theme item shows dialog`() {
        val sampleState = SettingsUiState(appTheme = AppTheme.LIGHT.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {})
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_app_theme_title))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_app_theme_dialog_title))
            .assertIsDisplayed()

        for (theme in AppTheme.entries) {
            val name = context.getString(theme.displayName)

            composeTestRule.onNode(
                hasText(name) and hasAnyAncestor(isDialog())
            ).assertIsDisplayed()
        }
    }

    @Test
    fun `clicking currency item shows dialog`() {
        val sampleState = SettingsUiState(currency = Currencies.EUR.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_currency))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_select_currency))
            .assertIsDisplayed()

        for (currency in Currencies.entries) {
            val name = context.getString(currency.displayName)

            composeTestRule.onNode(
                hasText(name) and hasAnyAncestor(isDialog())
            ).assertIsDisplayed()
        }
    }

    @Test
    fun `clicking unit item shows dialog`() {
        val sampleState = SettingsUiState(temperatureUnit = TemperatureSystem.METRIC.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_unit_item_title))
            .performClick()
        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_unit_dialog_title))
            .assertIsDisplayed()
        for (unit in TemperatureSystem.entries) {
            val name = context.getString(unit.displayName)

            composeTestRule.onNode(
                hasText(name) and hasAnyAncestor(isDialog())
            ).assertIsDisplayed()
        }
    }

    @Test
    fun `changing language calls callback with correct value`() {

        var selectedLanguageCode: String? = null
        val sampleState = SettingsUiState(language = AppLanguage.ENGLISH.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = { selectedLanguageCode = it },
                    onSetTheme = {},
                    onSetCurrency = {},
                    onSetTemperatureUnit = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_language_title))
            .performClick()

        val finnishText = LocaleHelper.getLanguageName(AppLanguage.FINNISH.code)

        composeTestRule.onNode(
            hasText(finnishText) and hasAnyAncestor(isDialog())
        ).performClick()

        composeTestRule.onNodeWithText(context.getString(android.R.string.ok))
            .performClick()

        assertEquals(AppLanguage.FINNISH.code, selectedLanguageCode)
    }

    @Test
    fun `dismissing currency dialog does not call callback`() {
        var callbackCalled = false
        val sampleState = SettingsUiState(currency = Currencies.EUR.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = {},
                    onSetCurrency = { callbackCalled = true },
                    onSetTemperatureUnit = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_currency))
            .performClick()

        composeTestRule.onNodeWithText(context.getString(android.R.string.cancel))
            .performClick()

        assertFalse(callbackCalled)

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_select_currency))
            .assertDoesNotExist()
    }

    @Test
    fun `changing theme calls callback with correct value`() {
        var selectedThemeCode: String? = null
        val sampleState = SettingsUiState(appTheme = AppTheme.LIGHT.code)

        composeTestRule.setContent {
            WeatherAppTheme {
                SettingsScreenContent(
                    uiState = sampleState,
                    onSetLanguage = {},
                    onSetTheme = { selectedThemeCode = it },
                    onSetCurrency = {},
                    onSetTemperatureUnit = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.settings_screen_app_theme_title))
            .performClick()

        val darkThemeText = context.getString(AppTheme.DARK.displayName)

        composeTestRule.onNode(
            hasText(darkThemeText) and hasAnyAncestor(isDialog())
        ).performClick()

        composeTestRule.onNodeWithText(context.getString(android.R.string.ok))
            .performClick()

        assertEquals(selectedThemeCode, AppTheme.DARK.code)
    }
}