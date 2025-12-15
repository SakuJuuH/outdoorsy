package com.example.outdoorsy.ui.weather.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.outdoorsy.R
import com.example.outdoorsy.ui.components.SectionTitle
import com.example.outdoorsy.ui.weather.model.WeatherData
import com.example.outdoorsy.utils.TemperatureSystem

@Composable
internal fun WeatherDetailsGrid(weatherData: WeatherData, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(
            title = stringResource(R.string.weather_details)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Default.WaterDrop,
                label = stringResource(id = R.string.humidity),
                value = "${weatherData.humidity}%",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Default.Air,
                label = stringResource(id = R.string.wind_speed),
                value = "${weatherData.windSpeed} ${
                    when (weatherData.unit) {
                        TemperatureSystem.METRIC.code -> "m/s"
                        else -> "mph"
                    }
                }",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Default.Visibility,
                label = stringResource(id = R.string.visibility),
                value = "${weatherData.visibility} km",
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Default.Speed,
                label = stringResource(id = R.string.pressure),
                value = "${weatherData.pressure} hPa",
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WeatherDetailCard(
                icon = Icons.Default.WbSunny,
                label = stringResource(id = R.string.sunrise),
                value = weatherData.sunrise,
                modifier = Modifier.weight(1f)
            )
            WeatherDetailCard(
                icon = Icons.Default.WbTwilight,
                label = stringResource(id = R.string.sunset),
                value = weatherData.sunset,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
