package com.example.outdoorsy.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Spacing(private val base: Dp = 4.dp) {
    operator fun invoke(multiplier: Int): Dp = (base.value * multiplier).dp
}

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val androidx.compose.material3.MaterialTheme.spacing: Spacing
    @Composable get() = LocalSpacing.current


