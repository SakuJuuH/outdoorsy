package com.example.outdoorsy.ui.shopping.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.theme.spacing

@Composable
internal fun RecommendedItemsSection(items: List<EbayItem>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        SectionTitle(
            title = stringResource(id = R.string.shopping_screen_recommended_items_section_title),
            modifier = Modifier.padding(horizontal = MaterialTheme.spacing(2))
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing(2))
        ) {
            items(items) { item ->
                ProductCard(
                    item = item,
                    modifier = Modifier.size(width = 300.dp, height = 200.dp)
                )
            }
        }
    }
}
