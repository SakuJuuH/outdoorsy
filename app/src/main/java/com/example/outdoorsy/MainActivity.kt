package com.example.outdoorsy

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.outdoorsy.ui.main.MainViewModel
import com.example.outdoorsy.ui.navigation.AppNavHost
import com.example.outdoorsy.ui.theme.WeatherAppTheme
import com.example.outdoorsy.utils.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewmodel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appTheme by viewmodel.appTheme.collectAsState()

            val isDarkTheme = when (appTheme) {
                AppTheme.LIGHT.code -> false
                AppTheme.DARK.code -> true
                else -> isSystemInDarkTheme()
            }

            WeatherAppTheme(darkTheme = isDarkTheme, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    AppNavHost(navController = navController)
                }
            }
        }
    }
}
