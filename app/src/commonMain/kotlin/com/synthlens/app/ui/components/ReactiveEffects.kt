package com.synthlens.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.sin

@Composable
fun rememberReactiveAmplitude(
    amplitude: Float,
    smoothing: Float = 0.3f
): Float {
    var smoothed by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(amplitude) {
        smoothed = smoothed + (amplitude - smoothed) * smoothing
    }
    return smoothed.coerceIn(0f, 1f)
}

@Composable
fun rememberReactiveGlow(
    amplitude: Float,
    baseIntensity: Float = 0.1f,
    maxIntensity: Float = 0.8f
): Float {
    val reactive = rememberReactiveAmplitude(amplitude, 0.4f)
    return baseIntensity + reactive * (maxIntensity - baseIntensity)
}

@Composable
fun rememberReactiveBorderAlpha(
    amplitude: Float,
    baseAlpha: Float = 0.1f,
    maxAlpha: Float = 0.35f
): Float {
    val reactive = rememberReactiveAmplitude(amplitude, 0.35f)
    return baseAlpha + reactive * (maxAlpha - baseAlpha)
}

@Composable
fun rememberBreathingAlpha(
    amplitude: Float,
    baseAlpha: Float = 0.4f,
    breatheAmount: Float = 0.08f
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        ),
        label = "breatheValue"
    )
    val reactive = rememberReactiveAmplitude(amplitude, 0.3f)
    val breathCycle = sin(breathe * Math.PI.toFloat() * 2) * breatheAmount
    return (baseAlpha + reactive * 0.15f + breathCycle).coerceIn(0.2f, 0.7f)
}

fun Modifier.reactiveScale(
    amplitude: Float,
    baseScale: Float = 1f,
    scaleAmount: Float = 0.02f
): Modifier {
    val reactive = if (amplitude > 0.01f) amplitude * scaleAmount else 0f
    return this.graphicsLayer {
        scaleX = baseScale + reactive
        scaleY = baseScale + reactive
    }
}

fun Modifier.reactiveGlow(
    amplitude: Float,
    glowColor: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF00E5FF),
    maxElevation: Float = 12f
): Modifier {
    val elevation = amplitude * maxElevation
    return this.graphicsLayer {
        this.shadowElevation = elevation
    }
}
