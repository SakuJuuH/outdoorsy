package com.example.outdoorsy.ui.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.ScreenTitle
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.shopping.components.ProductCard
import com.example.outdoorsy.ui.shopping.components.RecommendedItemsSection
import com.example.outdoorsy.ui.theme.WeatherAppTheme

@Composable
fun ShoppingScreen(modifier: Modifier = Modifier, viewModel: ShoppingViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. Header
        item {
            ScreenTitle(title = stringResource(id = R.string.shopping_screen_title))
        }

        // 2. Recommended Items
        if (uiState.recommendedItems.isNotEmpty()) {
            item {
                RecommendedItemsSection(items = uiState.recommendedItems)
            }
        }

        // 3. All Items Title
        item {
            SectionTitle(
                title = stringResource(id = R.string.shopping_screen_all_items_section_title),
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // 4. Loading State
        if (uiState.isLoading) {
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

        // 5. Error State
        if (uiState.error != null) {
            item {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // 6. Product List
        items(uiState.items) { item ->
            ProductCard(item = item)
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
