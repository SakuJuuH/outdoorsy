package com.example.outdoorsy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.viewmodel.DailyForecast
import com.example.outdoorsy.viewmodel.WeatherData
import com.example.outdoorsy.viewmodel.WeatherViewModel
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel(), modifier: Modifier = Modifier) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val pagerState = rememberPagerState(pageCount = { locations.size })
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            onFocusChange = { viewModel.setShowRecentSearches(it) },
            onSearch = { viewModel.searchLocation(it) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Weather Carousel (HorizontalPager)
        if (locations.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                WeatherCard(
                    weatherData = locations[page],
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Page Indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(locations.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color, CircleShape)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weather Details Grid
            Text(
                text = stringResource(id = R.string.weather_screen_weather_detail_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeatherDetailCard(
                        icon = Icons.Default.WaterDrop,
                        label = stringResource(id = R.string.weather_screen_weather_detail_humidity),
                        value = "${locations[pagerState.currentPage].humidity}%",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherDetailCard(
                        icon = Icons.Default.Air,
                        label = stringResource(id = R.string.weather_screen_weather_detail_wind_speed),
                        value = "${locations[pagerState.currentPage].windSpeed} km/h",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeatherDetailCard(
                        icon = Icons.Default.Visibility,
                        label = stringResource(id = R.string.weather_screen_weather_detail_visibility),
                        value = "${locations[pagerState.currentPage].visibility} mi",
                        modifier = Modifier.weight(1f)
                    )
                    WeatherDetailCard(
                        icon = Icons.Default.Speed,
                        label = stringResource(id = R.string.weather_screen_weather_detail_pressure),
                        value = "${locations[pagerState.currentPage].pressure} in",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WeatherDetailCard(
                        icon = Icons.Default.WbSunny,
                        label = stringResource(id = R.string.weather_screen_weather_detail_sunrise),
                        value = locations[pagerState.currentPage].sunrise,
                        modifier = Modifier.weight(1f)
                    )
                    WeatherDetailCard(
                        icon = Icons.Default.WbSunny,
                        label = stringResource(id = R.string.weather_screen_weather_detail_sunset),
                        value = locations[pagerState.currentPage].sunset,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5-Day Forecast
            Text(
                text = stringResource(id = R.string.weather_screen_weather_detail_five_day_forecast),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ForecastCard(forecast = locations[pagerState.currentPage].forecast)

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ForecastCard(forecast: List<DailyForecast>, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            forecast.forEach { day ->
                ForecastDayItem(dailyForecast = day)
            }
        }
    }
}

@Composable
fun ForecastDayItem(dailyForecast: DailyForecast, modifier: Modifier = Modifier) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)    ) {
        val (dayText, conditionRow, highTempText, lowTempText) = createRefs()

        val startGuideline = createGuidelineFromStart(0.3f)

        // 3. Day Text
        Text(
            text = dailyForecast.day,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(dayText) {
                start.linkTo(parent.start, margin = 16.dp)
                centerVerticallyTo(parent)
            }
        )

        // 4. Temperatures
        Text(
            text = "${dailyForecast.high}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(highTempText) {
                end.linkTo(parent.end, margin = 16.dp)
                centerVerticallyTo(parent)
            }
        )
        Text(
            text = "${dailyForecast.low}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.constrainAs(lowTempText) {
                end.linkTo(highTempText.start, margin = 16.dp)
                centerVerticallyTo(parent)
            }
        )

        // 5. Condition Row
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.constrainAs(conditionRow) {
                start.linkTo(startGuideline)
                centerVerticallyTo(parent)
            }
        ) {
            if (dailyForecast.icon.isNotBlank()) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${dailyForecast.icon}@2x.png",
                    contentDescription = dailyForecast.condition,
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = dailyForecast.condition,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Text(
                text = dailyForecast.condition,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        //placeholder used to be label (default in Material3)
        placeholder = { Text(stringResource(id = R.string.weather_screen_search_bar_hint)) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .onFocusChanged { onFocusChange(it.isFocused) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.weather_screen_search_bar_icon_description
                )
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
            onDone = { onSearch(query) }
        )
    )
}

@Composable
fun WeatherCard(weatherData: WeatherData, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Location
            Text(
                text = weatherData.location,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weather icon - load from OpenWeather
            if (weatherData.icon.isNotBlank()) {
                AsyncImage(
                    model = "https://openweathermap.org/img/wn/${weatherData.icon}@4x.png",
                    contentDescription = weatherData.condition,
                    modifier = Modifier.size(72.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Cloud,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Temperature
            Text(
                text = "${weatherData.temp}°C",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            // Condition
            Text(
                text = weatherData.condition,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // High/Low
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "L: ${weatherData.low}°",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
                Text(
                    text = "H: ${weatherData.high}°",
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
