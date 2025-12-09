package com.example.outdoorsy.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    // --- MAIN BRAND & ACCENT COLORS ---
    primary = pineGreen, // Using the lighter pineGreen as the main interactive color.
    secondary = forestGreen, // The darker forestGreen works well for less prominent actions.
    tertiary = accentGold, // Gold remains a vibrant accent.

    // --- BACKGROUND & SURFACE COLORS ---
    background = appBackgroundDark, // A very dark green, creating a softer dark background.
    surface = forestGreen, // Dark green for cards, making them distinct from the background.

    // --- CONTAINER COLORS ---
    // For larger, branded sections.
    primaryContainer = Color(0xFF1b4332), // The darkest green for the main WeatherCard background.
    surfaceVariant = Color(0xFF2d6a4f), // A slightly lighter dark green for the Forecast card.

    // --- TEXT & ICON COLORS ---
    // Text colors are inverted to be light.
    onPrimary = textOnDarkPrimary, // White text on primary buttons (pineGreen).
    onSecondary = textOnDarkPrimary, // White text on secondary buttons (forestGreen).
    onTertiary = textPrimary, // Dark text on the bright gold accent.

    onBackground = textOnDarkPrimary, // Near-white text on the dark green background.
    onSurface = textOnDarkPrimary, // Near-white text on dark green cards.

    onPrimaryContainer = textOnDarkPrimary, // White text/icons on the main WeatherCard.
    onSurfaceVariant = textOnDarkSecondary // Near-white text on the forecast card.
)

private val LightColorScheme = lightColorScheme(
    // --- MAIN BRAND & ACCENT COLORS ---
    primary = forestGreen,
    secondary = pineGreen,
    tertiary = accentGold,

    // --- BACKGROUND & SURFACE COLORS ---
    background = appBackgroundLight, // The main screen background
    surface = cardBackground, // Color for smaller cards like WeatherDetailCard

    // --- CONTAINER COLORS ---
    primaryContainer = forestGreen, // Sets the background for the large top WeatherCard
    surfaceVariant = stoneGray, // Sets the background for the 5-Day Forecast card

    // --- TEXT & ICON COLORS ---
    // These define what color text/icons should be ON TOP OF the colors above.

    onPrimary = Color.White, // Text on a primary color button
    onSecondary = Color.White, // Text on a secondary color button
    onTertiary = textPrimary, // Text on a tertiary color surface

    onBackground = textPrimary, // General text on the main screen background
    onSurface = textPrimary, // General text on a card (surface)

    onPrimaryContainer = Color.White, // Text/Icons on the main WeatherCard
    onSurfaceVariant = textPrimary, // Text/Icons on the 5-Day Forecast card

    surfaceContainer = offWhite, // Used by Navigation Bar
    surfaceContainerHigh = cardBackground, // Used by Search Bar (Docked)
    surfaceContainerHighest = offWhite,
    surfaceContainerLow = appBackgroundLight,
    surfaceContainerLowest = Color.White,

    inverseSurface = textPrimary,
    inverseOnSurface = Color.White
)

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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
