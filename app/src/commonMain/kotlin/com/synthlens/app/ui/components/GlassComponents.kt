package com.synthlens.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.synthlens.app.ui.theme.*

@Composable
fun GlassPanel(
    modifier: Modifier = Modifier,
    alpha: Float = 0.5f,
    cornerRadius: Dp = 16.dp,
    borderAlpha: Float = 0.15f,
    glowColor: Color = SynthCyan,
    glowIntensity: Float = 0f,
    content: @Composable () -> Unit
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .shadow(
                elevation = if (glowIntensity > 0f) (4 + glowIntensity * 8).dp else 4.dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = glowIntensity * 0.3f * boost),
                spotColor = glowColor.copy(alpha = glowIntensity * 0.2f * boost)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (alpha * 0.12f * boost).coerceIn(0f, 0.8f)),
                        Color.White.copy(alpha = (alpha * 0.06f * boost).coerceIn(0f, 0.6f)),
                        Color.White.copy(alpha = (alpha * 0.03f * boost).coerceIn(0f, 0.4f))
                    )
                )
            )
            .border(
                width = if (isBright) 1.5.dp else 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White.copy(alpha = (borderAlpha * 1.5f * boost).coerceIn(0f, 0.8f)),
                        Color.White.copy(alpha = (borderAlpha * 0.5f * boost).coerceIn(0f, 0.5f)),
                        Color.White.copy(alpha = (borderAlpha * 0.2f * boost).coerceIn(0f, 0.3f))
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                ),
                shape = shape
            ),
        content = { content() }
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    alpha: Float = 0.45f,
    cornerRadius: Dp = 16.dp,
    reactiveAmplitude: Float = 0f,
    content: @Composable () -> Unit
) {
    val safeAmplitude = if (reactiveAmplitude.isFinite()) reactiveAmplitude else 0f
    val animatedAlpha by animateFloatAsState(
        targetValue = (alpha + safeAmplitude * 0.15f).let { v -> if (v.isFinite()) v.coerceIn(0f, 1f) else 0f },
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "glassAlpha"
    )

    val glowIntensity by animateFloatAsState(
        targetValue = (safeAmplitude * 0.6f).let { v -> if (v.isFinite()) v.coerceIn(0f, 1f) else 0f },
        animationSpec = tween(200, easing = FastOutSlowInEasing),
        label = "glowIntensity"
    )

    GlassPanel(
        modifier = modifier,
        alpha = animatedAlpha,
        cornerRadius = cornerRadius,
        glowIntensity = glowIntensity,
        content = content
    )
}

@Composable
fun GlassFloatingButton(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    reactiveAmplitude: Float = 0f,
    content: @Composable () -> Unit
) {
    val safeAmplitude = if (reactiveAmplitude.isFinite()) reactiveAmplitude else 0f
    val glowIntensity by animateFloatAsState(
        targetValue = if (isActive) ((0.4f + safeAmplitude * 0.3f).let { v -> if (v.isFinite()) v.coerceIn(0f, 1f) else 0f }) else 0f,
        animationSpec = tween(300),
        label = "btnGlow"
    )

    GlassPanel(
        modifier = modifier,
        alpha = if (isActive) 0.55f else 0.35f,
        cornerRadius = 14.dp,
        borderAlpha = if (isActive) 0.3f else 0.1f,
        glowColor = SynthCyan,
        glowIntensity = glowIntensity,
        content = content
    )
}

@Composable
fun GlassChip(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(20.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (isSelected)
                    SynthCyan.copy(alpha = 0.12f)
                else
                    Color.White.copy(alpha = 0.05f)
            )
            .border(
                width = 0.5.dp,
                color = if (isSelected)
                    SynthCyan.copy(alpha = 0.4f)
                else
                    Color.White.copy(alpha = 0.08f)
            ),
        content = { content() }
    )
}

fun Modifier.glassBackground(
    alpha: Float = 0.45f,
    cornerRadius: Dp = 16.dp
): Modifier {
    val shape = RoundedCornerShape(cornerRadius)
    return this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                colors = listOf(
                    Color.White.copy(alpha = alpha * 0.1f),
                    Color.White.copy(alpha = alpha * 0.05f)
                )
            )
        )
        .border(
            width = 0.5.dp,
            brush = Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.04f)
                )
            ),
            shape = shape
        )
}
