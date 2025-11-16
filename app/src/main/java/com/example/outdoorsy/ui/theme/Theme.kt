package com.example.outdoorsy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = forestGreen,
    secondary = pineGreen,
    tertiary = accentGold,
)

private val LightColorScheme = lightColorScheme(
    // Main brand colors, used for buttons, icons, etc.
    primary = forestGreen,
    secondary = pineGreen,
    tertiary = accentGold,

    // Background colors
    background = appBackground,       // The main screen background
    surface = cardBackground,    // Color for smaller cards like WeatherDetailCard

    // Container colors - These are crucial for WeatherScreen
    primaryContainer = forestGreen, // Sets the background for the large top WeatherCard
    surfaceVariant = stoneGray,   // Sets the background for the 5-Day Forecast card

    // --- TEXT & ICON COLORS ---
    // These define what color text/icons should be ON TOP OF the colors above.

    onPrimary = Color.White,         // Text on a primary color button
    onSecondary = Color.White,       // Text on a secondary color button
    onTertiary = textPrimary,        // Text on a tertiary color surface

    onBackground = textPrimary,      // General text on the main screen background
    onSurface = textPrimary,         // General text on a card (surface)

    // Crucial for WeatherScreen
    onPrimaryContainer = Color.White, // Text/Icons on the main WeatherCard
    onSurfaceVariant = textPrimary    // Text/Icons on the 5-Day Forecast card
)

    /* Other default colors to override
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onPrimary = darkGrey1,

     */

@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
