package com.example.outdoorsy.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.data.repository.SettingsRepository
import com.example.outdoorsy.domain.model.Location as LocationModel
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.usecase.GetCurrentWeather
import com.example.outdoorsy.domain.usecase.GetForecast
import com.example.outdoorsy.ui.mappers.toUiModel
import com.example.outdoorsy.ui.model.WeatherData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeather,
    private val getForecast: GetForecast,
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _showRecentSearches = MutableStateFlow(false)
    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    private val settingsFlow = combine(
        settingsRepository.getTemperatureUnit(),
        settingsRepository.getLanguage()
    ) { units, language ->
        CurrentSettings(
            units,
            language
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrentSettings("metric", "en"))

    private val lastGpsLocation = MutableStateFlow<LocationModel?>(null)
    private val gpsWeather = MutableStateFlow<WeatherData?>(null)
    private val savedWeather = MutableStateFlow<List<WeatherData?>>(emptyList())

    private val _locationAddedEvent = MutableSharedFlow<String>()

    val searchQuery: StateFlow<String> = _searchQuery
    val showRecentSearches: StateFlow<Boolean> = _showRecentSearches
    val recentSearches: StateFlow<List<String>> = _recentSearches
    val isLoading: StateFlow<Boolean> = _isLoading
    val weatherList: StateFlow<List<WeatherData?>> = combine(
        gpsWeather,
        savedWeather
    ) { gpsItem, savedList ->
        if (gpsItem != null) {
            val gpsCityName = gpsItem.location

            val filteredSavedList = savedList.filterNot { savedItem ->
                savedItem?.location.equals(gpsCityName, ignoreCase = true)
            }

            val displayGpsItem = gpsItem.copy(
                isCurrentLocation = true
            )

            listOf(displayGpsItem) + filteredSavedList
        } else {
            savedList
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val locationAddedEvent = _locationAddedEvent.asSharedFlow()

    init {
        getAllSavedLocations()
        loadCurrentLocationWeather()
        loadRecentSearches()
    }

    fun loadCurrentLocationWeather() {
        viewModelScope.launch {
            val location: LocationModel? = locationRepository.getCurrentLocation()
            lastGpsLocation.value = location
            if (location == null) {
                gpsWeather.value = null
                return@launch
            }
            settingsFlow.collectLatest { settings ->
                _isLoading.value = true
                Log.d(
                    "WeatherViewModel",
                    "Fetching weather data with units: ${settings.unit} and language: ${settings.lang}"
                )
                try {
                    gpsWeather.value = getWeatherData(location, settings)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    private fun getAllSavedLocations() {
        viewModelScope.launch {
            combine(
                locationRepository.getAllLocations(),
                settingsFlow
            ) { locations, settings ->
                Pair(locations, settings)
            }.collectLatest { (locations, settings) ->
                _isLoading.value = true
                Log.d(
                    "WeatherViewModel",
                    "Fetching weather data for $locations locations"
                )
                val deferredWeatherList = locations.map { location ->
                    async {
                        try {
                            Log.d(
                                "WeatherViewModel",
                                "Fetching weather data for location: ${location.name} with units: ${settings.unit} and language: ${settings.lang}"
                            )
                            getWeatherData(location, settings)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            null
                        }
                    }
                }

                savedWeather.value = deferredWeatherList.awaitAll().filterNotNull()
                _isLoading.value = false
            }
        }
    }

    private suspend fun getWeatherData(
        location: LocationModel,
        settings: CurrentSettings
    ): WeatherData {
        val current = getCurrentWeather(
            city = location.name,
            lat = location.latitude,
            lon = location.longitude,
            units = settings.unit,
            language = settings.lang
        )
        val forecast = getForecast(
            city = location.name,
            lat = location.latitude,
            lon = location.longitude,
            units = settings.unit,
            language = settings.lang
        )

        return current.toUiModel(forecast.listOfForecastItems, settings.unit)
    }

    private fun loadRecentSearches() {
        _recentSearches.value = listOf("Paris", "Berlin", "Barcelona", "Amsterdam", "Rome")
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShowRecentSearches(show: Boolean) {
        _showRecentSearches.value = show
    }

    fun searchAndAddLocation(city: String) {
        if (city.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true

            try {
                val foundLocation = locationRepository.getCoordinatesByLocation(city)

                val currentGpsLocationName = gpsWeather.value?.location

                if (foundLocation?.name.equals(currentGpsLocationName, ignoreCase = true)) {
                    Log.d(
                        "WeatherViewModel",
                        "GPS location already exists: $currentGpsLocationName"
                    )
                    _locationAddedEvent.emit(foundLocation?.name ?: city)
                    return@launch
                }
                if (foundLocation != null) {
                    locationRepository.saveLocation(foundLocation)
                }
                _locationAddedEvent.emit(foundLocation?.name ?: city)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeLocation(locationName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("WeatherViewModel", "Removing location: $locationName")
                locationRepository.deleteByName(locationName)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addRecentSearch(location: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(location)
        current.add(0, location)
        _recentSearches.value = current.take(5)
    }

    private data class CurrentSettings(val unit: String, val lang: String)
}
