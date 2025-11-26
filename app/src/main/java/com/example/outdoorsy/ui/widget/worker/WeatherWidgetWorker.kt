package com.example.outdoorsy.ui.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.appwidget.updateAll
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.outdoorsy.domain.repository.LocationRepository
import com.example.outdoorsy.domain.repository.WeatherRepository
import com.example.outdoorsy.ui.widget.WeatherWidget
import com.example.outdoorsy.ui.widget.model.WeatherWidgetData
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first

@HiltWorker
class WeatherWidgetWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val weatherRepository: WeatherRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = try {
        val primaryData = fetchPrimaryWeatherData()

        val dataList = if (primaryData != null) listOf(primaryData) else emptyList()
        val jsonString = Gson().toJson(dataList)

        Log.d("WeatherWidgetWorker", "JSON String: $jsonString")

        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(WeatherWidget::class.java)

        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId) { prefs ->
                prefs[WeatherWidget.WEATHER_DATA_KEY] = jsonString
                Log.d(
                    "WeatherWidgetWorker",
                    "Updated Widget State: ${prefs[WeatherWidget.WEATHER_DATA_KEY]}"
                )
            }
        }
        WeatherWidget().updateAll(context)

        Result.success()
    } catch (e: CancellationException) {
        Log.e("WeatherWidgetWorker", "Worker cancelled", e)
        throw e
    } catch (e: Exception) {
        Log.e("WeatherWidgetWorker", "Error fetching weather data", e)
        Result.failure()
    }

    private suspend fun fetchPrimaryWeatherData(): WeatherWidgetData? {
        // Determine Target: Priority is GPS -> First Saved Location
        val gpsLocation = locationRepository.getCurrentLocation()

        val targetParams = if (gpsLocation != null) {
            LocationParams(gpsLocation.latitude, gpsLocation.longitude, null, true)
        } else {
            // Fallback to the first saved location if GPS is off/unavailable
            val firstSaved = locationRepository.getAllLocations().first().firstOrNull()
            if (firstSaved != null) {
                Log.d("WeatherWidgetWorker", "Using Saved Location: ${firstSaved.name}")
                LocationParams(firstSaved.latitude, firstSaved.longitude, firstSaved.name, false)
            } else {
                return null // No locations found at all
            }
        }

        return try {
            val response = weatherRepository.getCurrentWeatherByCoordinates(
                targetParams.lat,
                targetParams.lon,
                units = "metric",
                lang = "en"
            )

            // Pre-load Icon for instant display
            val iconCode = response.weather.firstOrNull()?.icon
            if (!iconCode.isNullOrBlank()) {
                val request = ImageRequest.Builder(context)
                    .data("https://openweathermap.org/img/wn/$iconCode@2x.png")
                    .build()
                ImageLoader(context).execute(request)
            }

            // Return Data
            val weatherWidgetData = WeatherWidgetData(
                location = targetParams.name ?: response.name,
                temperature = response.main.temperature.toInt(),
                condition =
                response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "",
                high = response.main.maxTemperature.toInt(),
                low = response.main.minTemperature.toInt(),
                icon = iconCode ?: "",
                unit = "metric",
                isCurrentLocation = targetParams.isGps
            )
            Log.d("WeatherWidgetWorker", "Fetched Weather Data: $weatherWidgetData")

            weatherWidgetData
        } catch (e: Exception) {
            Log.e("WeatherWidgetWorker", "Error fetching weather API", e)
            null
        }
    }

    private data class LocationParams(
        val lat: Double,
        val lon: Double,
        val name: String?,
        val isGps: Boolean
    )
}
