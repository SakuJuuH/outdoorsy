package com.example.outdoorsy.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.outdoorsy.ui.activity.ActivityScreen
import com.example.outdoorsy.ui.activity.ActivityViewModel
import com.example.outdoorsy.ui.components.AppBottomNavBar
import com.example.outdoorsy.ui.history.HistoryScreen
import com.example.outdoorsy.ui.history.HistoryViewModel
import com.example.outdoorsy.ui.navigation.Screen
import com.example.outdoorsy.ui.settings.SettingsScreen
import com.example.outdoorsy.ui.settings.SettingsViewModel
import com.example.outdoorsy.ui.shopping.ShoppingScreen
import com.example.outdoorsy.ui.shopping.ShoppingViewModel
import com.example.outdoorsy.ui.weather.WeatherScreen
import com.example.outdoorsy.ui.weather.WeatherViewModel

@Composable
fun AppContainer(mainNavController: NavHostController) {
    val nestedNavController = rememberNavController()

    Scaffold(bottomBar = {
        AppBottomNavBar(navController = nestedNavController)
    }) { paddingValues ->
        NavHost(
            navController = nestedNavController,
            startDestination = Screen.AppNav.Weather.route,
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            enterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(200))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200))
            }
        ) {
            composable(Screen.AppNav.Weather.route) {
                val viewModel: WeatherViewModel = hiltViewModel()
                WeatherScreen(viewModel = viewModel)
            }

            composable(Screen.AppNav.History.route) {
                val viewModel: HistoryViewModel = hiltViewModel()
                HistoryScreen(viewModel = viewModel)
            }

            composable(Screen.AppNav.Activity.route) {
                val viewModel: ActivityViewModel = hiltViewModel()
                ActivityScreen(viewModel = viewModel, navController = nestedNavController)
            }

            composable(Screen.AppNav.Shopping.route) {
                val viewModel: ShoppingViewModel = hiltViewModel()
                ShoppingScreen(viewModel = viewModel)
            }

            composable(Screen.AppNav.Settings.route) {
                val viewModel: SettingsViewModel = hiltViewModel()
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
