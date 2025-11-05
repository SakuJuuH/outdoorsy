package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class WeatherViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showRecentSearches = MutableStateFlow(false)
    val showRecentSearches: StateFlow<Boolean> = _showRecentSearches

    private val _locations = MutableStateFlow<List<WeatherData>>(emptyList())
    val locations: StateFlow<List<WeatherData>> = _locations

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    init {
        loadMockData()
        loadRecentSearches()
    }

    private fun loadMockData() {
        _locations.value = listOf(
            WeatherData(
                location = "Helsinki",
                temp = 5,
                condition = "Cloudy",
                high = 7,
                low = 3,
                humidity = 70,
                windSpeed = 15,
                visibility = 8.0,
                pressure = 29.9,
                sunrise = "8:15 AM",
                sunset = "3:45 PM",
                forecast = listOf(
                    DailyForecast("Mon", 8, 4, "Cloudy"),
                    DailyForecast("Tue", 6, 2, "Rainy"),
                    DailyForecast("Wed", 9, 5, "Partly Cloudy"),
                    DailyForecast("Thu", 7, 3, "Sunny"),
                    DailyForecast("Fri", 10, 6, "Clear")
                )
            ),
            WeatherData(
                location = "New York",
                temp = 18,
                condition = "Sunny",
                high = 22,
                low = 15,
                humidity = 55,
                windSpeed = 10,
                visibility = 10.0,
                pressure = 30.1,
                sunrise = "6:45 AM",
                sunset = "5:30 PM",
                forecast = listOf(
                    DailyForecast("Mon", 23, 16, "Sunny"),
                    DailyForecast("Tue", 20, 14, "Partly Cloudy"),
                    DailyForecast("Wed", 19, 13, "Cloudy"),
                    DailyForecast("Thu", 21, 15, "Clear"),
                    DailyForecast("Fri", 24, 17, "Sunny")
                )
            ),
            WeatherData(
                location = "Tokyo",
                temp = 21,
                condition = "Rainy",
                high = 24,
                low = 18,
                humidity = 80,
                windSpeed = 20,
                visibility = 5.0,
                pressure = 29.7,
                sunrise = "6:20 AM",
                sunset = "5:15 PM",
                forecast = listOf(
                    DailyForecast("Mon", 22, 17, "Rainy"),
                    DailyForecast("Tue", 23, 18, "Cloudy"),
                    DailyForecast("Wed", 25, 19, "Partly Cloudy"),
                    DailyForecast("Thu", 26, 20, "Sunny"),
                    DailyForecast("Fri", 24, 19, "Clear")
                )
            ),
            WeatherData(
                location = "London",
                temp = 12,
                condition = "Partly Cloudy",
                high = 15,
                low = 9,
                humidity = 65,
                windSpeed = 12,
                visibility = 7.0,
                pressure = 30.0,
                sunrise = "7:30 AM",
                sunset = "4:45 PM",
                forecast = listOf(
                    DailyForecast("Mon", 14, 8, "Cloudy"),
                    DailyForecast("Tue", 16, 10, "Rainy"),
                    DailyForecast("Wed", 13, 9, "Cloudy"),
                    DailyForecast("Thu", 15, 11, "Partly Cloudy"),
                    DailyForecast("Fri", 17, 12, "Sunny")
                )
            ),
            WeatherData(
                location = "Sydney",
                temp = 25,
                condition = "Clear",
                high = 28,
                low = 22,
                humidity = 45,
                windSpeed = 8,
                visibility = 12.0,
                pressure = 30.2,
                sunrise = "5:50 AM",
                sunset = "7:45 PM",
                forecast = listOf(
                    DailyForecast("Mon", 29, 23, "Sunny"),
                    DailyForecast("Tue", 27, 21, "Clear"),
                    DailyForecast("Wed", 26, 22, "Sunny"),
                    DailyForecast("Thu", 28, 23, "Partly Cloudy"),
                    DailyForecast("Fri", 30, 24, "Clear")
                )
            )
        )
    }

    private fun loadRecentSearches() {
        _recentSearches.value = listOf(
            "Paris",
            "Berlin",
            "Barcelona",
            "Amsterdam",
            "Rome"
        )
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setShowRecentSearches(show: Boolean) {
        _showRecentSearches.value = show
    }

    fun addRecentSearch(location: String) {
        val current = _recentSearches.value.toMutableList()
        current.remove(location)
        current.add(0, location)
        _recentSearches.value = current.take(5)
    }
}

data class WeatherData(
    val location: String,
    val temp: Int,
    val condition: String,
    val high: Int,
    val low: Int,
    val humidity: Int,
    val windSpeed: Int,
    val visibility: Double,
    val pressure: Double,
    val sunrise: String,
    val sunset: String,
    val forecast: List<DailyForecast>
)

data class DailyForecast(
    val day: String,
    val high: Int,
    val low: Int,
    val condition: String
)