package com.example.outdoorsy.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.ui.components.ButtonType
import com.example.outdoorsy.ui.components.CustomButton
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing
import com.example.outdoorsy.viewmodel.ShoppingViewModel

@Composable
fun ShoppingScreen(modifier: Modifier = Modifier, viewModel: ShoppingViewModel = hiltViewModel()) {
    viewModel.uiState.collectAsState()

    val uiState = viewModel.uiState.collectAsState()
    val isLoading = uiState.value.isLoading
    val error = uiState.value.error
    val items = uiState.value.items

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title section
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = MaterialTheme.spacing(4),
                        vertical = MaterialTheme.spacing(3)
                    )
            ) {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing(2)))
                Text(
                    text = stringResource(id = R.string.shopping_screen_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(MaterialTheme.spacing(1)))
            }
        }

        // --- Recommended Items Section ---
        item {
            Text(
                text = stringResource(
                    id = R.string.shopping_screen_recommended_items_section_title
                ),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // --- All Items Section ---
        item {
            // Add a spacer for visual separation before the next section
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(id = R.string.shopping_screen_all_items_section_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Handle loading state
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        // 3. Handle Error State
        if (error != null) {
            item {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 4. Display the items from the API
        items(items) { item ->
            ProductCard(
                item = item
            )
        }
    }
}

@Composable
fun ProductCard(item: EbayItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.title,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )
                    // You can display category if available
                    if (item.categoryNames.isNotEmpty()) {
                        Text(
                            text = item.categoryNames.first(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${
                        when (item.price.currency) {
                            "USD" -> "$"
                            "GBP" -> "£"
                            else -> ""
                        }
                    }${item.price.value}${if (item.price.currency == "EUR") "€" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                CustomButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, item.link.toUri())
                        context.startActivity(intent)
                    },
                    text = stringResource(id = R.string.shopping_screen_view_listing_button),
                    type = ButtonType.PRIMARY
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 380)
@Composable
fun ShoppingScreenPreview() {
    WeatherAppTheme {
        ShoppingScreen()
    }
}
