package com.example.outdoorsy.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.ui.history.model.ConditionRating
import com.example.outdoorsy.ui.theme.spacing

@Composable
internal fun ConditionRatingPill(condition: ConditionRating, modifier: Modifier = Modifier) {
    val backgroundColor = when (condition) {
        ConditionRating.EXCELLENT -> Color(0xFF4CAF50)

        // Green
        ConditionRating.VERY_GOOD -> Color(0xFF8BC34A)

        // Light Green
        ConditionRating.GOOD -> Color(0xFF2196F3) // Blue
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = MaterialTheme.spacing(2), vertical = MaterialTheme.spacing(1)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = condition.displayName,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            imageVector = Icons.Filled.ExpandMore,
            contentDescription = "Expand",
            modifier = Modifier.size(16.dp),
            tint = Color.White
        )
    }
}
