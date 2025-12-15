package com.example.outdoorsy.ui.weather

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.theme.spacing
import com.example.outdoorsy.ui.weather.components.ForecastCard
import com.example.outdoorsy.ui.weather.components.WeatherCard
import com.example.outdoorsy.ui.weather.components.WeatherDetailsGrid
import com.example.outdoorsy.ui.weather.components.WeatherPageIndicator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: WeatherViewModel = hiltViewModel()) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()
    val locations by viewModel.weatherList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val pagerState = rememberPagerState(pageCount = { locations.size })
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
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

    var searchBarActive by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.clearFocusEvent.collectLatest {
            searchBarActive = false
            keyboardController?.hide()
        }
    }

    // Scroll to new location when added
    LaunchedEffect(locations) {
        viewModel.locationAddedEvent.collect { newLocationName ->
            val newIndex = locations.indexOfFirst { weatherData ->
                weatherData?.location.equals(newLocationName, ignoreCase = true)
            }

            if (newIndex != -1) {
                scope.launch {
                    pagerState.animateScrollToPage(newIndex)
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- Search Bar Section ---
            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onSearch = { query ->
                            if (query.trim().isNotBlank()) {
                                viewModel.searchAndAddLocation(query.trim())
                                viewModel.updateSearchQuery("")
                                searchBarActive = false
                                viewModel.setShowRecentSearches(false)
                            }
                        },
                        expanded = searchBarActive,
                        onExpandedChange = {
                            searchBarActive = it
                            viewModel.setShowRecentSearches(it)
                        },
                        placeholder = {
                            Text(stringResource(id = R.string.search_city_or_location))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (searchBarActive && searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.updateSearchQuery("") }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear search"
                                    )
                                }
                            } else {
                                IconButton(onClick = {
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
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.MyLocation,
                                        contentDescription = stringResource(
                                            R.string.use_current_location
                                        ),
                                        tint = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    )
                },
                expanded = searchBarActive,
                onExpandedChange = {
                    searchBarActive = it
                    viewModel.setShowRecentSearches(it)
                    if (!it) {
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (recentSearches.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        item {
                            Text(
                                text = stringResource(id = R.string.recent),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(recentSearches) { location ->
                            ListItem(
                                headlineContent = { Text(location) },
                                leadingContent = {
                                    Icon(Icons.Default.History, contentDescription = null)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .padding(vertical = MaterialTheme.spacing(1))
                                    .clickable {
                                        viewModel.updateSearchQuery("")
                                        viewModel.searchAndAddLocation(location)
                                        searchBarActive = false
                                        viewModel.setShowRecentSearches(false)
                                    }
                            )
                        }
                    }
                } else if (searchBarActive) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Start typing to search for a location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                Column {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // --- Main Content ---
                    if (locations.isEmpty() && !isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.add_location_for_weather),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    if (locations.isNotEmpty() && !isLoading) {
                        // 1. Horizontal Pager for Main Cards
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

                        // 2. Page Indicator
                        WeatherPageIndicator(
                            pagerState = pagerState,
                            locations = locations
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Details and Forecast
                        if (currentWeatherData != null) {
                            WeatherDetailsGrid(weatherData = currentWeatherData)

                            Spacer(modifier = Modifier.height(16.dp))

                            SectionTitle(
                                title = stringResource(
                                    id = R.string.five_day_forecast
                                ),
                                modifier = Modifier.padding(start = 16.dp)
                            )

                            ForecastCard(forecast = currentWeatherData.forecast)

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
                if (searchBarActive) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) {
                                searchBarActive = false
                                viewModel.setShowRecentSearches(false)
                                keyboardController?.hide()
                            }
                    )
                }
            }
        }
    }
}
