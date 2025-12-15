package com.example.outdoorsy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBasket
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.outdoorsy.R

sealed class Screen(val route: String) {
    object AppContainer : Screen("app_container")

    sealed class AppNav(route: String, val title: Int, val icon: ImageVector) : Screen(route) {
        object Weather :
            AppNav("weather_screen", R.string.weather, Icons.Outlined.Cloud)

        object History :
            AppNav("history_screen", R.string.history, Icons.Outlined.History)

        object Activity :
            AppNav(
                "activity_screen",
                R.string.activity,
                icon = Icons.Outlined.TaskAlt
            )

        object Shopping : AppNav(
            "shopping_screen",
            R.string.shopping,
            Icons.Outlined.ShoppingBasket
        )

        object Settings :
            AppNav("settings_screen", R.string.settings, Icons.Outlined.Settings)
    }
}

val bottomNavItems = listOf(
    Screen.AppNav.Weather,
    Screen.AppNav.History,
    Screen.AppNav.Activity,
    Screen.AppNav.Shopping,
    Screen.AppNav.Settings
)
