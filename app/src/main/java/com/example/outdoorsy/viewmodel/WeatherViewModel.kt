package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class WeatherViewModel : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery


    private val _locations = MutableStateFlow<List<WeatherData>>(emptyList())
    val locations: StateFlow<List<WeatherData>> = _locations


    init {
        loadMockData()
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
                sunset = "3:45 PM"
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
                sunset = "5:30 PM"
            )
        )
    }


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
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
    val sunset: String
)

