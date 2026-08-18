package com.synthlens.app.ui.components

import androidx.compose.runtime.*

@Composable
fun rememberAmbientBrightness(): Float {
    return 300f
}

fun isOutdoorBrightness(lux: Float): Boolean = lux > 10000f
fun isBrightEnvironment(lux: Float): Boolean = lux > 5000f
