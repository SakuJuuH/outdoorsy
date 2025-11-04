package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.viewmodel.WeatherData
import com.example.outdoorsy.viewmodel.WeatherViewModel


@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    modifier: Modifier = Modifier
    ) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val pagerState = rememberPagerState(pageCount = { locations.size })


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { },
            placeholder = { Text("Search for a city or location...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )


        Spacer(modifier = Modifier.height(24.dp))


        if (locations.isNotEmpty()) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
                WeatherCard(weatherData = locations[page], modifier = Modifier.padding(horizontal = 8.dp))
            }


            Spacer(modifier = Modifier.height(16.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(locations.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) MaterialTheme.colorScheme.primary else Color.Gray
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .clip(MaterialTheme.shapes.small)
                            .background(color)
                    )
                }
            }
        }
    }
}


@Composable
fun WeatherCard(weatherData: WeatherData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(weatherData.location, style = MaterialTheme.typography.titleLarge)
            Icon(Icons.Default.Cloud, contentDescription = null, modifier = Modifier.size(64.dp))
            Text(text = "${weatherData.temp}°C", style = MaterialTheme.typography.displayMedium)
            Text(weatherData.condition, style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("H: ${weatherData.high}°")
                Text("L: ${weatherData.low}°")
            }

            Spacer(modifier = Modifier.height(16.dp))

            val weatherDetails = listOf(
                Pair(Icons.Default.WaterDrop, "Humidity: ${weatherData.humidity}%"),
                Pair(Icons.Default.Air, "Wind: ${weatherData.windSpeed} km/h")
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(weatherDetails) { (icon, label) ->
                    WeatherDetailCard(icon, label)
                }
            }
        }
    }
}




@Composable
fun WeatherDetailCard(icon: ImageVector, label: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


