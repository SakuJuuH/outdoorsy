package com.example.outdoorsy.ui.widget

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.outdoorsy.data.model.WeatherWidgetData
import com.example.outdoorsy.domain.repository.WeatherRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import com.example.outdoorsy.data.local.dao.LocationDao


@EntryPoint
@InstallIn(SingletonComponent::class)
interface WeatherWidgetEntryPoint {
    fun locationDao(): LocationDao
    fun weatherRepository(): WeatherRepository
}

class WeatherWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetEntryPoint::class.java
        )

        val locationDao = entryPoint.locationDao()
        val weatherRepository = entryPoint.weatherRepository()

        val weatherDataList = buildWeatherDataList(context, locationDao, weatherRepository)

        provideContent {
            val prefs = currentState<Preferences>()
            val currentPage = prefs[CURRENT_PAGE_KEY] ?: 0
            val validPage = currentPage.coerceIn(0, (weatherDataList.size - 1).coerceAtLeast(0))

            GlanceTheme {
                if (weatherDataList.isNotEmpty()) {
                    WeatherWidgetContent(
                        weatherDataList = weatherDataList,
                        currentPage = validPage
                    )
                } else {
                    EmptyWidgetContent()
                }
            }
        }
    }

    suspend fun buildWeatherDataList(
        context: Context,
        locationDao: LocationDao,
        weatherRepository: WeatherRepository
    ): List<Pair<WeatherWidgetData, Bitmap?>> {
        val weatherDataList = mutableListOf<Pair<WeatherWidgetData, Bitmap?>>()

        try {
            // 1. Try to get current GPS location weather first
            val gpsWeather = getGpsWeatherData(context, weatherRepository)
            if (gpsWeather != null) {
                weatherDataList.add(gpsWeather)
            }

            // 2. Get saved locations from database
            val locations = locationDao.getAll().first()
            val currentGpsLocation = weatherDataList.firstOrNull()?.first?.location

            locations.forEach { location ->
                // Skip if this location is the same as GPS location
                if (location.name.equals(currentGpsLocation, ignoreCase = true)) {
                    return@forEach
                }

                try {
                    val response = weatherRepository.getCurrentWeatherByCoordinates(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        units = "metric",
                        lang = "en"
                    )

                    val iconCode = response.weather.firstOrNull()?.icon ?: ""
                    val iconBitmap = loadWeatherIcon(context, iconCode)

                    weatherDataList.add(
                        Pair(
                            WeatherWidgetData(
                                location = location.name,
                                temperature = response.main.temperature.toInt(),
                                condition = response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                                high = response.main.maxTemperature.toInt(),
                                low = response.main.minTemperature.toInt(),
                                icon = iconCode,
                                unit = "metric"
                            ),
                            iconBitmap
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return weatherDataList
    }

    private suspend fun getGpsWeatherData(
        context: Context,
        weatherRepository: WeatherRepository
    ): Pair<WeatherWidgetData, Bitmap?>? {
        return try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? android.location.LocationManager
                val lastLocation = locationManager?.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER)
                    ?: locationManager?.getLastKnownLocation(android.location.LocationManager.NETWORK_PROVIDER)

                if (lastLocation != null) {
                    val response = weatherRepository.getCurrentWeatherByCoordinates(
                        latitude = lastLocation.latitude,
                        longitude = lastLocation.longitude,
                        units = "metric",
                        lang = "en"
                    )

                    val iconCode = response.weather.firstOrNull()?.icon ?: ""
                    val iconBitmap = loadWeatherIcon(context, iconCode)

                    Pair(
                        WeatherWidgetData(
                            location = response.name,
                            temperature = response.main.temperature.toInt(),
                            condition = response.weather.firstOrNull()?.description?.replaceFirstChar { it.uppercase() } ?: "",
                            high = response.main.maxTemperature.toInt(),
                            low = response.main.minTemperature.toInt(),
                            icon = iconCode,
                            unit = "metric"
                        ),
                        iconBitmap
                    )
                } else null
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun loadWeatherIcon(context: Context, iconCode: String): Bitmap? {
        return if (iconCode.isNotBlank()) {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data("https://openweathermap.org/img/wn/${iconCode}@2x.png")
                    .build()
                (imageLoader.execute(request).drawable as? BitmapDrawable)?.bitmap
            } catch (e: Exception) {
                null
            }
        } else null
    }

    @Composable
    private fun WeatherWidgetContent(
        weatherDataList: List<Pair<WeatherWidgetData, Bitmap?>>,
        currentPage: Int
    ) {
        val (weatherData, iconBitmap) = weatherDataList.getOrNull(currentPage)
            ?: return EmptyWidgetContent()

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .clickable(actionRunCallback<NextPageAction>())
        ) {
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weatherData.location,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                if (iconBitmap != null) {
                    Image(
                        provider = ImageProvider(iconBitmap),
                        contentDescription = weatherData.condition,
                        modifier = GlanceModifier.size(48.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = "${weatherData.temperature}°C",
                    style = TextStyle(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Text(
                    text = weatherData.condition,
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                Row(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "L: ${weatherData.low}°",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    Text(
                        text = "H: ${weatherData.high}°",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                }

                if (weatherDataList.size > 1) {
                    Spacer(modifier = GlanceModifier.height(8.dp))
                    PageIndicators(currentPage = currentPage, totalPages = weatherDataList.size)
                }
            }
        }
    }

    @Composable
    private fun PageIndicators(currentPage: Int, totalPages: Int) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            repeat(totalPages.coerceAtMost(5)) { index ->
                val isSelected = index == currentPage
                Spacer(
                    modifier = GlanceModifier
                        .size(if (isSelected) 8.dp else 6.dp)
                        .background(
                            if (isSelected)
                                GlanceTheme.colors.primary
                            else
                                GlanceTheme.colors.onPrimaryContainer
                        )
                )
                if (index < totalPages.coerceAtMost(5) - 1) {
                    Spacer(modifier = GlanceModifier.width(4.dp))
                }
            }
        }
    }

    @Composable
    private fun EmptyWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "No locations",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = GlanceTheme.colors.onPrimaryContainer
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Add a city in the app",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = GlanceTheme.colors.onPrimaryContainer
                )
            )
        }
    }

    companion object {
        val CURRENT_PAGE_KEY = intPreferencesKey("current_page")
    }
}

class NextPageAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val entryPoint = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WeatherWidgetEntryPoint::class.java
            )
            val locationDao = entryPoint.locationDao()
            val weatherRepository = entryPoint.weatherRepository()

            val weatherDataList = WeatherWidget().buildWeatherDataList(
                context,
                locationDao,
                weatherRepository
            )

            val totalLocations = weatherDataList.size

            val currentPage = prefs[WeatherWidget.CURRENT_PAGE_KEY] ?: 0
            val nextPage = (currentPage + 1) % totalLocations.coerceAtLeast(1)
            prefs[WeatherWidget.CURRENT_PAGE_KEY] = nextPage
        }
        WeatherWidget().update(context, glanceId)
    }
}
