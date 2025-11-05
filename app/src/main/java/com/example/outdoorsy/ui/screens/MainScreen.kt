package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.ui.components.BottomNavBar
import com.example.outdoorsy.ui.components.NavItem
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun MainScreen(modifier: Modifier) {
    var selected by remember { mutableStateOf(NavItem.SEARCH) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                modifier = modifier.padding(bottom = 6.dp),
                selectedItem = selected,
                onItemClick = { selected = it }
            )
        }
    ) { innerPadding: PaddingValues ->
        val contentModifier = Modifier.padding(innerPadding)
        when (selected) {
            NavItem.SEARCH -> SearchScreen(modifier = contentModifier)
            NavItem.WEATHER -> WeatherScreen(modifier = contentModifier)
            NavItem.HISTORY -> PlaceholderScreen("History", modifier = contentModifier)
            NavItem.ACTIVITY -> ActivityScreen(modifier = contentModifier)
            NavItem.SHOPPING -> ShoppingScreen()
            NavItem.SETTINGS -> SettingsScreen(modifier = contentModifier.padding())
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(MaterialTheme.spacing(4))
    )
}
