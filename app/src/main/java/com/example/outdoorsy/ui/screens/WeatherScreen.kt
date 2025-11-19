package com.example.outdoorsy.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.model.DailyForecast
import com.example.outdoorsy.ui.model.WeatherData
import com.example.outdoorsy.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val locations by viewModel.weatherList.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val pagerState = rememberPagerState(pageCount = { locations.size })
    val scope = rememberCoroutineScope()

    val context = LocalContext.current

    val currentWeatherData = locations.getOrNull(pagerState.currentPage)

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            viewModel.loadCurrentLocationWeather()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.locationAddedEvent.collect { newLocationName ->
            val newIndex = locations.indexOfFirst { weatherData ->
                weatherData?.location.equals(newLocationName, ignoreCase = true)
            }
        }
    }

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
            onSearch = {
                viewModel.searchAndAddLocation(it.trim())
                viewModel.addRecentSearch(it.trim())
                viewModel.updateSearchQuery("")
            },
            onLocationClick = {
                val hasFine = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val hasCoarse = ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (!hasFine && !hasCoarse) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                } else {
                    viewModel.loadCurrentLocationWeather()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(64.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Weather Carousel (HorizontalPager)
        if (locations.isEmpty() && !isLoading) {
            Text(
                "Add a location to see the weather!",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (locations.isNotEmpty() && !isLoading) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val weatherData = locations.getOrNull(page) ?: return@HorizontalPager
                WeatherCard(
                    weatherData = weatherData,
                    onRemoveClick = { locationName ->
                        viewModel.removeLocation(locationName)
                    },
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
                    val weatherData = locations[iteration] ?: return@repeat
                    val isSelected = pagerState.currentPage == iteration
                    val isGpsPage = weatherData.isCurrentLocation
                    val color = if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }

                    Box(
                        modifier = Modifier.padding(4.dp).size(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isGpsPage) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = stringResource(
                                    R.string.weather_screen_gps_icon_description
                                ),
                                modifier = Modifier.fillMaxSize(),
                                tint = color
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(color, CircleShape)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weather Details Grid
            Text(
                text = stringResource(id = R.string.weather_screen_weather_detail_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            if (currentWeatherData != null) {
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
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_humidity
                            ),
                            value = "${currentWeatherData.humidity}%",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDetailCard(
                            icon = Icons.Default.Air,
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_wind_speed
                            ),
                            value = "${currentWeatherData.windSpeed} ${
                                when (currentWeatherData.unit) {
                                    "metric" -> "m/s"
                                    else -> "mph"
                                }
                            }",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherDetailCard(
                            icon = Icons.Default.Visibility,
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_visibility
                            ),
                            value = "${currentWeatherData.visibility} km",
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDetailCard(
                            icon = Icons.Default.Speed,
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_pressure
                            ),
                            value = "${currentWeatherData.pressure} hPa",
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        WeatherDetailCard(
                            icon = Icons.Default.WbSunny,
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_sunrise
                            ),
                            value = currentWeatherData.sunrise,
                            modifier = Modifier.weight(1f)
                        )
                        WeatherDetailCard(
                            icon = Icons.Default.WbSunny,
                            label = stringResource(
                                id = R.string.weather_screen_weather_detail_sunset
                            ),
                            value = currentWeatherData.sunset,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 5-Day Forecast
                Text(
                    text = stringResource(
                        id = R.string.weather_screen_weather_detail_five_day_forecast
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                ForecastCard(forecast = currentWeatherData.forecast)

                Spacer(modifier = Modifier.height(16.dp))
            }
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
            .padding(vertical = 4.dp)
    ) {
        val (dayText, conditionRow, highTempText, lowTempText) = createRefs()

        val startConditionGuideline = createGuidelineFromStart(0.22f)

        val startLowTempGuideline = createGuidelineFromStart(0.8f)

        Text(
            text = dailyForecast.day,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(dayText) {
                start.linkTo(parent.start)
                centerVerticallyTo(parent)
                end.linkTo(startConditionGuideline, margin = 16.dp)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = "${dailyForecast.high}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.constrainAs(highTempText) {
                end.linkTo(parent.end)
                centerVerticallyTo(parent)
            }
        )

        Text(
            text = "${dailyForecast.low}°",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.constrainAs(lowTempText) {
                start.linkTo(startLowTempGuideline)
                centerVerticallyTo(parent)
            }
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.constrainAs(conditionRow) {
                // Pin its start to the day text guideline
                start.linkTo(startConditionGuideline)
                // Pin its end to the low temp guideline
                end.linkTo(startLowTempGuideline, margin = 16.dp)
                // Fill the space between the two guidelines
                width = Dimension.fillToConstraints
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    onLocationClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(stringResource(id = R.string.weather_screen_search_bar_hint)) },
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .shadow(elevation = 8.dp, shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
                if (focusState.isFocused) {
                    keyboardController?.show()
                }
            },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(
                    id = R.string.weather_screen_search_bar_icon_description
                )
            )
        },
        trailingIcon = {
            IconButton(onClick = onLocationClick) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = stringResource(
                        R.string.weather_screen_search_bar_location_icon_description
                    ),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
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
        keyboardActions = KeyboardActions(
            onDone = {
                onSearch(query)
                keyboardController?.hide()
            }
        )
    )
}

@Composable
fun WeatherCard(
    weatherData: WeatherData,
    onRemoveClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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
                    text = "${weatherData.temp}${
                        when (weatherData.unit) {
                            "metric" -> "°C"
                            else -> "°F"
                        }
                    }",
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

            if (!weatherData.isCurrentLocation) {
                IconButton(
                    onClick = { onRemoveClick(weatherData.location) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(
                            id = R.string.weather_screen_remove_location_description
                        ),
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
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
