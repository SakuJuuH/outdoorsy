package com.example.outdoorsy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.outdoorsy.ui.screens.MainScreen
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherAppTheme (dynamicColor = false) {
                MainScreen(Modifier)
            }
        }
    }
}

@Preview
@Composable
fun WeatherAppPreview() {
    WeatherAppTheme {
        MainScreen(Modifier)
    }
}
