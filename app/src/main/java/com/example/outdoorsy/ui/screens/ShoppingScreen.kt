package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.ui.components.ButtonType
import com.example.outdoorsy.ui.components.CustomButton
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.viewmodel.ShoppingItem
import com.example.outdoorsy.viewmodel.ShoppingViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ShoppingScreen(
    shoppingViewModel: ShoppingViewModel = viewModel()
) {
    val recommendedItems by shoppingViewModel.recommendedItems.collectAsState()
    val allItems by shoppingViewModel.allItems.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Main Page Title
        item {
            Text(
                text = "Weather Gear Shop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // --- Recommended Items Section ---
        item {
            Text(
                text = "Recommended for Current Weather",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Display "Recommended" items in a vertical list
        items(recommendedItems) { item ->
            ProductCard(
                item = item,
                onAddToCartClicked = { shoppingViewModel.onAddToCart(item) }
            )
        }

        // --- All Items Section ---
        item {
            // Add a spacer for visual separation before the next section
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "All Items",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // Display "All Items" in a vertical list
        items(allItems) { item ->
            ProductCard(
                item = item,
                onAddToCartClicked = { shoppingViewModel.onAddToCart(item) }
            )
        }
    }
}

@Composable
fun ProductCard(
    item: ShoppingItem,
    onAddToCartClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Spacer(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale.US).format(item.price)
                Text(
                    text = formattedPrice,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                CustomButton(
                    onClick = onAddToCartClicked,
                    text = "Add to Cart",
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
