package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
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
            WeatherData("Helsinki", 5, "Cloudy", 7, 3, 70, 15),
            WeatherData("New York", 18, "Sunny", 22, 15, 55, 10),
            WeatherData("Tokyo", 21, "Rainy", 24, 18, 80, 20)
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
    val windSpeed: Int
)

