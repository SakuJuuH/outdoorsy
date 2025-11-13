package com.example.outdoorsy.ui.screens

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
import com.example.outdoorsy.ui.components.AppBottomNavBar
import com.example.outdoorsy.ui.navigation.Screen
import com.example.outdoorsy.viewmodel.ActivityViewModel
import com.example.outdoorsy.viewmodel.HistoryViewModel
import com.example.outdoorsy.viewmodel.SettingsViewModel
import com.example.outdoorsy.viewmodel.ShoppingViewModel
import com.example.outdoorsy.viewmodel.WeatherViewModel

@Composable
fun AppContainer(mainNavController: NavHostController) {
    val nestedNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavBar(navController = nestedNavController)
        }
    ) { paddingValues ->

        NavHost(
            navController = nestedNavController,
            startDestination = Screen.AppNav.Weather.route,
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
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
                ActivityScreen(viewModel = viewModel)
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
