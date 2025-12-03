package com.example.outdoorsy.ui.shopping.components

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.outdoorsy.R
import com.example.outdoorsy.domain.model.ebay.EbayItem
import com.example.outdoorsy.ui.components.ButtonType
import com.example.outdoorsy.ui.components.CustomButton
import com.example.outdoorsy.utils.Currencies

@Composable
internal fun ProductCard(item: EbayItem, modifier: Modifier = Modifier) {
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
                    contentDescription = stringResource(
                        id = R.string.shopping_screen_product_image_description,
                        item.title
                    ),
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
                            Currencies.USD.code -> Currencies.USD.symbol
                            Currencies.GBP.code -> Currencies.GBP.symbol
                            else -> ""
                        }
                    }${item.price.value} " +
                        if (item.price.currency ==
                            Currencies.EUR.code
                        ) {
                            Currencies.EUR.symbol
                        } else {
                            ""
                        },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    // change to onSurface for better contrast
                    color = MaterialTheme.colorScheme.onSurface
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
