package com.example.outdoorsy.ui.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
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

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WeatherWidgetEntryPoint::class.java
        )

        val locationDao = entryPoint.locationDao()
        val weatherRepository = entryPoint.weatherRepository()

        val weatherData = try {
            val locations = locationDao.getAll().first()
            val firstLocation = locations.firstOrNull()

            if (firstLocation != null) {
                val response = weatherRepository.getCurrentWeatherByCoordinates(
                    latitude = firstLocation.latitude,
                    longitude = firstLocation.longitude,
                    units = "metric",
                    lang = "en"
                )

                WeatherWidgetData(
                    location = firstLocation.name,
                    temperature = response.main.temperature.toInt(),
                    condition = response.weather.firstOrNull()?.description
                        ?.replaceFirstChar { it.uppercase() } ?: "Unknown",
                    high = response.main.maxTemperature.toInt(),
                    low = response.main.minTemperature.toInt(),
                    icon = response.weather.firstOrNull()?.icon ?: "",
                    unit = "metric"
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

        // Load weather condition from OpenWeatherMap
        val iconBitmap = weatherData?.icon?.takeIf { it.isNotBlank() }?.let { iconCode ->
            try {
                val imageLoader = coil.ImageLoader(context)
                val request = coil.request.ImageRequest.Builder(context)
                    .data("https://openweathermap.org/img/wn/${iconCode}@2x.png")
                    .build()

                (imageLoader.execute(request).drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap
            } catch (e: Exception) {
                null
            }
        }

        provideContent {
            GlanceTheme {
                WeatherWidgetContent(weatherData, iconBitmap)
            }
        }
    }

    @Composable
    private fun WeatherWidgetContent(data: WeatherWidgetData?, iconBitmap: android.graphics.Bitmap?) {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (data != null) {
                Text(
                    text = data.location,
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                // Display weather icon from bitmap
                if (iconBitmap != null) {
                    Image(
                        provider = ImageProvider(iconBitmap),
                        contentDescription = data.condition,
                        modifier = GlanceModifier.size(48.dp)
                    )
                }

                Spacer(modifier = GlanceModifier.height(8.dp))

                Text(
                    text = "${data.temperature}°C",
                    style = TextStyle(
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Text(
                    text = data.condition,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )

                Spacer(modifier = GlanceModifier.height(8.dp))

                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "L: ${data.low}°",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                    Spacer(modifier = GlanceModifier.width(16.dp))
                    Text(
                        text = "H: ${data.high}°",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                }
            } else {
                Text(
                    text = "No weather data",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.onPrimaryContainer
                    )
                )
            }
        }
    }

}
