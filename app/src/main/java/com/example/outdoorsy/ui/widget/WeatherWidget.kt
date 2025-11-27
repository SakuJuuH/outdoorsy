package com.example.outdoorsy.ui.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.glance.ColorFilter
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import coil.ImageLoader
import coil.request.ImageRequest
import com.example.outdoorsy.MainActivity
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.widget.model.WeatherWidgetData
import com.google.gson.Gson

class WeatherWidget : GlanceAppWidget() {
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition

    private val gson = Gson()

    companion object {
        val WEATHER_DATA_KEY = stringPreferencesKey("weather_data_json")
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        Log.d("WeatherWidget", "provideGlance CALLED for ID: $id")

        val prefs =
            getAppWidgetState(context.applicationContext, PreferencesGlanceStateDefinition, id)
        val jsonString = prefs[WEATHER_DATA_KEY]

        Log.d("WeatherWidget", "Loaded JSON String: $jsonString")

        val currentData: WeatherWidgetData? = if (jsonString != null) {
            try {
                gson.fromJson(jsonString, WeatherWidgetData::class.java)
            } catch (e: Exception) {
                Log.e("WeatherWidget", "Error parsing JSON", e)
                null
            }
        } else {
            null
        }

        Log.d("WeatherWidget", "Loaded Weather Data: $currentData")

        val currentBitmap = if (currentData != null) {
            loadWeatherIcon(context.applicationContext, currentData.icon)
        } else {
            null
        }

        provideContent {
            GlanceTheme {
                if (currentData != null) {
                    WeatherWidgetContent(
                        data = currentData,
                        iconBitmap = currentBitmap
                    )
                } else {
                    EmptyWidgetContent()
                }
            }
        }
    }

    private suspend fun loadWeatherIcon(context: Context, iconCode: String): Bitmap? {
        if (iconCode.isBlank()) return null
        return try {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data("https://openweathermap.org/img/wn/$iconCode@2x.png")
                .allowHardware(false)
                .build()
            val result = imageLoader.execute(request).drawable
            (result as? BitmapDrawable)?.bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Composable
    private fun WeatherWidgetContent(data: WeatherWidgetData, iconBitmap: Bitmap?) {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .padding(16.dp)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (data.isCurrentLocation) {
                        Image(
                            provider = ImageProvider(R.drawable.ic_location_on),
                            contentDescription = "GPS",
                            colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimaryContainer),
                            modifier = GlanceModifier.size(14.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(6.dp))
                    }
                    Text(
                        text = data.location,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = GlanceTheme.colors.onPrimaryContainer
                        ),
                        maxLines = 1
                    )
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (iconBitmap != null) {
                        Image(
                            provider = ImageProvider(iconBitmap),
                            contentDescription = data.condition,
                            modifier = GlanceModifier.size(56.dp)
                        )
                        Spacer(modifier = GlanceModifier.width(12.dp))
                    }

                    Text(
                        text = "${data.temperature}°",
                        style = TextStyle(
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = GlanceTheme.colors.onPrimaryContainer
                        )
                    )
                }

                Spacer(modifier = GlanceModifier.height(12.dp))

                Text(
                    text = data.condition,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = GlanceTheme.colors.onPrimaryContainer
                    ),
                    maxLines = 1
                )

                Spacer(modifier = GlanceModifier.height(4.dp))

                Text(
                    text = "H: ${data.high}°  L: ${data.low}°",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        }
    }

    @Composable
    private fun EmptyWidgetContent() {
        val context = LocalContext.current

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.primaryContainer)
                .clickable(actionStartActivity<MainActivity>()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.widget_no_data),
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimaryContainer,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = context.getString(R.string.widget_tap_to_open),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = GlanceTheme.colors.onSurfaceVariant
                    )
                )
            }
        }
    }
}
