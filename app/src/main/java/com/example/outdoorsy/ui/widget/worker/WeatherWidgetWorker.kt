package com.example.outdoorsy.ui.widget.worker

import android.content.Context
import android.util.Log
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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

    private val gson = Gson()

    override suspend fun doWork(): Result = try {
        val primaryData = fetchPrimaryWeatherData()

        val jsonString = gson.toJson(primaryData)

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
            WeatherWidget().update(context, glanceId)
        }

        Result.success()
    } catch (e: CancellationException) {
        Log.e("WeatherWidgetWorker", "Worker cancelled", e)
        throw e
    } catch (e: Exception) {
        Log.e("WeatherWidgetWorker", "Error fetching weather data", e)
        Result.failure()
    }

    private suspend fun fetchPrimaryWeatherData(): WeatherWidgetData? {
        val gpsLocation = locationRepository.getCurrentLocation()

        val targetParams = if (gpsLocation != null) {
            LocationParams(gpsLocation.latitude, gpsLocation.longitude, null, true)
        } else {
            val firstSaved = locationRepository.getAllLocations().first().firstOrNull()
            if (firstSaved != null) {
                Log.d("WeatherWidgetWorker", "Using Saved Location: ${firstSaved.name}")
                LocationParams(firstSaved.latitude, firstSaved.longitude, firstSaved.name, false)
            } else {
                return null
            }
        }

        return try {
            val response = weatherRepository.getCurrentWeatherByCoordinates(
                targetParams.lat,
                targetParams.lon,
                units = "metric",
                lang = "en"
            )

            val weatherWidgetData = WeatherWidgetData(
                location = targetParams.name ?: response.name,
                temperature = response.main.temperature.toInt(),
                condition =
                response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() }
                    ?: "",
                high = response.main.maxTemperature.toInt(),
                low = response.main.minTemperature.toInt(),
                icon = response.weather.firstOrNull()?.icon ?: "",
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
