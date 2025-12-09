package com.example.outdoorsy.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun ScreenTitle(title: String, modifier: Modifier = Modifier, subtitle: String? = null) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = MaterialTheme.spacing(4),
                vertical = MaterialTheme.spacing(3)
            )
    ) {
        Spacer(modifier = Modifier.height(MaterialTheme.spacing(2)))

        // Main Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            // changed to onBackground from onSurface to better constrast
            color = MaterialTheme.colorScheme.onBackground
        )

        if (subtitle != null) {
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        } else {
            // Default spacer if no subtitle
            Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))
        }
    }
}
