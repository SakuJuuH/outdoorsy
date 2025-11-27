package com.example.outdoorsy.ui.settings.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
internal fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconContentDescription: String,
    onClick: () -> Unit
) {
    BaseSettingsItem(
        icon = icon,
        title = title,
        subtitle = subtitle,
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = iconContentDescription,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
