package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.ButtonType
import com.example.outdoorsy.ui.components.CustomButton
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.viewmodel.ShoppingItem
import com.example.outdoorsy.viewmodel.ShoppingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ShoppingScreen(modifier: Modifier = Modifier, viewModel: ShoppingViewModel = hiltViewModel()) {
    viewModel.uiState.collectAsState()

    /* Todo: Uncomment these variables and use these for the UI
    val isLoading = uiState.value.isLoading
    val error = uiState.value.error
    val items = uiState.value.items
     */

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Page Title
        item {
            Text(
                text = stringResource(id = R.string.shopping_screen_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
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
        /*
        // Display "Recommended" items in a vertical list
        items(uiStat) { item ->
            ProductCard(
                item = item,
                onAddToCartClicked = { viewModel.onAddToCart(item) }
            )
        }
         */
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
        /*
        // Display "All Items" in a vertical list
        items(allItems) { item ->
            ProductCard(
                item = item,
                onAddToCartClicked = { viewModel.onAddToCart(item) }
            )
        }

         */
    }
}

@Composable
fun ProductCard(item: ShoppingItem, onAddToCartClicked: () -> Unit, modifier: Modifier = Modifier) {
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
                // Placeholder for an image
                /* TODO: Replace the placeholder with AsyncImage for each item
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    modifier = some modifier
                )
                 */
                Spacer(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = item.description, style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Format price to currency
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(item)
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                CustomButton(
                    onClick = onAddToCartClicked,
                    text = stringResource(id = R.string.shopping_screen_add_to_cart_button),
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
