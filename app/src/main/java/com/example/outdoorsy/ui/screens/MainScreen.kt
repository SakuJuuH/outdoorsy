package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.outdoorsy.ui.components.BottomNavBar
import com.example.outdoorsy.ui.components.NavItem
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun MainScreen() {
    var selected by remember { mutableStateOf(NavItem.SEARCH) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomNavBar(
                selectedItem = selected,
                onItemClick = { selected = it }
            )
        }
    ) { innerPadding: PaddingValues ->
        val contentModifier = Modifier.padding(innerPadding)
        when (selected) {
            NavItem.SEARCH -> SearchScreen(modifier = contentModifier)
            NavItem.WEATHER -> PlaceholderScreen("Weather", modifier = contentModifier)
            NavItem.HISTORY -> PlaceholderScreen("History", modifier = contentModifier)
            NavItem.ACTIVITY -> PlaceholderScreen("Activity", modifier = contentModifier)
            NavItem.SHOPPING -> PlaceholderScreen("Shopping", modifier = contentModifier)
            NavItem.SETTINGS -> PlaceholderScreen("Settings", modifier = contentModifier)
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(text = title, modifier = modifier.padding(MaterialTheme.spacing(4)))
}