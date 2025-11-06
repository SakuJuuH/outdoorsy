package com.example.outdoorsy.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.theme.WeatherAppTheme

enum class NavItem {
    WEATHER,
    HISTORY,
    ACTIVITY,
    SHOPPING,
    SETTINGS
}

@Composable
fun BottomNavBar(
    selectedItem: NavItem,
    onItemClick: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val navItems = listOf(
        NavItem.WEATHER to context.getString(R.string.nav_weather),
        NavItem.HISTORY to context.getString(R.string.nav_history),
        NavItem.ACTIVITY to context.getString(R.string.nav_activity),
        NavItem.SHOPPING to context.getString(R.string.nav_shopping),
        NavItem.SETTINGS to context.getString(R.string.nav_settings)
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navItems.forEach { (item, label) ->
            NavItem(
                item = item,
                label = label,
                isSelected = selectedItem == item,
                onClick = { onItemClick(item) }
            )
        }
    }
}

@Composable
private fun NavItem(item: NavItem, label: String, isSelected: Boolean, onClick: () -> Unit) {
    val icon = when (item) {
        NavItem.WEATHER -> Icons.Outlined.Cloud
        NavItem.HISTORY -> Icons.Outlined.History
        NavItem.ACTIVITY -> Icons.Outlined.TaskAlt
        NavItem.SHOPPING -> Icons.Outlined.ShoppingCart
        NavItem.SETTINGS -> Icons.Outlined.Settings
    }

    val color = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.secondary
    }

    Box(
        modifier = Modifier
            .width(60.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier
                    .size(24.dp),
                tint = color
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BottomNavBarPreview() {
    WeatherAppTheme {
        BottomNavBar(
            selectedItem = NavItem.SETTINGS,
            onItemClick = { }
        )
    }
}
