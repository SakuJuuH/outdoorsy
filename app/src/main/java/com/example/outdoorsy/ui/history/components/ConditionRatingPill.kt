package com.example.outdoorsy.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.ui.theme.spacing

@Composable
internal fun ConditionRatingPill(
    suitabilityLabel: String,
    suitabilityScore: Int,
    modifier: Modifier = Modifier
) {
    // Colors match the Activity page's RecommendationCard suitability colors
    val (backgroundColor, textColor) = when (suitabilityScore) {
        1 -> Color(0xFFD32F2F) to Color.White
        2 -> Color(0xFFF44336) to Color.White
        3 -> Color(0xFFFFB300) to Color.Black
        4 -> Color(0xFF4CAF50) to Color.White
        5 -> Color(0xFF2E7D32) to Color.White
        else -> Color(0xFF9E9E9E) to Color.White
    }

    Row(
        modifier = modifier
            // add a border ensures the pill is visible even if the color matches the background
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = MaterialTheme.spacing(2), vertical = MaterialTheme.spacing(1)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = suitabilityLabel,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}
