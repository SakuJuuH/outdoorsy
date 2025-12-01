package com.example.outdoorsy.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.shopping.components.ProductCard
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.ui.theme.spacing

@Composable
fun ShoppingScreen(modifier: Modifier = Modifier, viewModel: ShoppingViewModel = hiltViewModel()) {
    // This tells the ViewModel to fetch data when the screen is first displayed.
    LaunchedEffect(key1 = Unit) {
        viewModel.fetchAllShoppingData()
    }

    val uiState = viewModel.uiState.collectAsState().value
    val isLoading = uiState.isLoading
    val error = uiState.error
    val items = uiState.items
    val recommendedItems = uiState.recommendedItems

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing(2))
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header
        item {
            ScreenTitle(title = stringResource(id = R.string.shopping_screen_title))
        }

        // --- Recommended Items Section ---
        if (recommendedItems.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(
                        id = R.string.shopping_screen_recommended_items_section_title
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(recommendedItems) { item ->
                ProductCard(item = item)
            }
        }

        // --- All Items Section ---
        item {
            SectionTitle(
                title = stringResource(id = R.string.shopping_screen_all_items_section_title),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // 4. Loading State
        // Improved logic: show loading indicator only when both lists are empty
        if (isLoading && items.isEmpty() && recommendedItems.isEmpty()) {
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

        // Handle Error State
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

@Preview(showBackground = true, widthDp = 380)
@Composable
fun ShoppingScreenPreview() {
    WeatherAppTheme {
        ShoppingScreen()
    }
}
