package com.example.outdoorsy.ui.activity.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable

@Composable
internal fun RecommendationCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    suitabilityLabel: String? = null,
    suitabilityScore: Int? = null,
    items: List<String>,
    onClothingClick: (() -> Unit)? = null
) {
    val cardModifier = if (onClothingClick != null) {
        Modifier.clickable { onClothingClick() }
    } else {
        Modifier
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(cardModifier)
            .padding(top = 16.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    //change from primary to onSurface for better visibility on light/dark themes
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (suitabilityLabel != null && suitabilityScore != null) {
                    Spacer(modifier = Modifier.width(12.dp))

                    val (bgColor, textColor) = when (suitabilityScore) {
                        1 -> Color(0xFFD32F2F) to Color.White
                        2 -> Color(0xFFF44336) to Color.White
                        3 -> Color(0xFFFFB300) to Color.Black
                        4 -> Color(0xFF4CAF50) to Color.White
                        5 -> Color(0xFF2E7D32) to Color.White
                        else -> Color(0xFF9E9E9E) to Color.White
                    }

                    Text(
                        text = suitabilityLabel,
                        color = textColor,
                        modifier = modifier
                            // add a border ensures the pill is visible even if the color matches the background
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(percent = 50)
                            )
                            .background(bgColor, shape = RoundedCornerShape(percent = 50))
                            .padding(horizontal = 12.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items.forEach { item ->
                    Row(verticalAlignment = Alignment.Top) {
                        Text("•", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = item,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
