package com.example.outdoorsy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.outdoorsy.domain.model.WeatherResponse
import com.example.outdoorsy.domain.usecase.GetCurrentWeather
import com.example.outdoorsy.domain.usecase.GetForecast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getCurrentWeather: GetCurrentWeather,
    private val getForecast: GetForecast
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _showRecentSearches = MutableStateFlow(false)
    val showRecentSearches: StateFlow<Boolean> = _showRecentSearches

    private val _locations = MutableStateFlow<List<WeatherData>>(emptyList())
    val locations: StateFlow<List<WeatherData>> = _locations

    private val _recentSearches = MutableStateFlow<List<String>>(emptyList())
    val recentSearches: StateFlow<List<String>> = _recentSearches

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        fetchWeatherDataForMultipleCities(listOf("Helsinki", "Paris", "New York", "Tokyo", "Sydney"))
    }

    private fun fetchWeatherDataForMultipleCities(cities: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val allWeatherData = mutableListOf<WeatherData>()
                cities.forEach { city ->
                    try {
                        val weatherResponse = getCurrentWeather(
                            city = city,
                            units = "metric",
                            language = "en"
                        )
                        val forecastResponse = getForecast(
                            city = city,
                            units = "metric",
                            language = "en"
                        )
                        val weatherData = mapToWeatherData(weatherResponse, forecastResponse.listOfForecastItems)
                        allWeatherData.add(weatherData)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                _locations.value = allWeatherData
                addRecentSearch(cities.first())
            } finally {
                _isLoading.value = false
            }
        }
    }


    private fun mapToWeatherData(response: WeatherResponse, forecastItems: List<com.example.outdoorsy.domain.model.ForecastItem>): WeatherData {
        val dailyForecasts = forecastItems
            .groupBy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.timeOfData.toLong() * 1000)) }
            .values
            .take(5)
            .map { dayItems ->
                val temps = dayItems.map { it.main.maxTemperature }
                DailyForecast(
                    day = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayItems.first().timeOfData.toLong() * 1000)),
                    high = temps.maxOrNull()?.toInt() ?: 0,
                    low = temps.minOrNull()?.toInt() ?: 0,
                    condition = dayItems.first().weather.firstOrNull()?.group ?: "Unknown"
                )
            }

        return WeatherData(
            location = response.name,
            temp = response.main.temperature.toInt(),
            condition = response.weather.firstOrNull()?.group ?: "Unknown",
            high = response.main.maxTemperature.toInt(),
            low = response.main.minTemperature.toInt(),
            humidity = response.main.humidity,
            windSpeed = response.wind.speed.toInt(),
            visibility = response.visibility / 1000.0,
            pressure = response.main.pressure / 10.0,
            sunrise = formatTime(response.sys.sunrise),
            sunset = formatTime(response.sys.sunset),
            forecast = dailyForecasts
        )
    }

    private fun formatTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp * 1000))
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

    fun searchLocation(city: String) {
        if (city.isNotBlank()) {
            fetchWeatherDataForMultipleCities(listOf(city))
            _searchQuery.value = ""
            _showRecentSearches.value = false
        }
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

data class DailyForecast(val day: String, val high: Int, val low: Int, val condition: String)
