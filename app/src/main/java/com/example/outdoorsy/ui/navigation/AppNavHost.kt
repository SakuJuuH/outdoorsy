package com.example.outdoorsy.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.outdoorsy.ui.main.AppContainer

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.AppContainer.route
    ) {
        composable(Screen.AppContainer.route) {
            AppContainer(mainNavController = navController)
        }
    }
}
