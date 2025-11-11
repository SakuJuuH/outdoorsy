package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.ui.components.BottomNavBar
import com.example.outdoorsy.ui.components.NavItem

@Composable
fun MainScreen(modifier: Modifier) {
    var selected by remember { mutableStateOf(NavItem.WEATHER) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                modifier = modifier
                    .padding(bottom = 12.dp),
                selectedItem = selected,
                onItemClick = { selected = it }
            )
        }
    ) { innerPadding: PaddingValues ->
        val contentModifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)
        when (selected) {
            NavItem.WEATHER -> WeatherScreen(modifier = contentModifier)
            NavItem.HISTORY -> HistoryScreen(modifier = contentModifier)
            NavItem.ACTIVITY -> ActivityScreen(modifier = contentModifier)
            NavItem.SHOPPING -> ShoppingScreen(modifier = contentModifier)
            NavItem.SETTINGS -> SettingsScreen(modifier = contentModifier)
        }
    }
}
