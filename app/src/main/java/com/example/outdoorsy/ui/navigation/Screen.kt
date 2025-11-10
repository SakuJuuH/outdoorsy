package com.example.outdoorsy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object AppContainer : Screen("app_container")

    sealed class AppNav(route: String, val title: String, val icon: ImageVector) : Screen(route) {
        object Weather : AppNav("weather_screen", "Weather", Icons.Outlined.Cloud)
        object History : AppNav("history_screen", "History", Icons.Outlined.History)
        object Activity : AppNav("activity_screen", "Activity", Icons.Outlined.TaskAlt)
        object Shopping : AppNav("shopping_screen", "Shopping", Icons.Outlined.ShoppingBasket)
        object Settings : AppNav("settings_screen", "Settings", Icons.Outlined.Settings)
    }
}

val bottomNavItems = listOf(
    Screen.AppNav.Weather,
    Screen.AppNav.History,
    Screen.AppNav.Activity,
    Screen.AppNav.Shopping,
    Screen.AppNav.Settings
)
