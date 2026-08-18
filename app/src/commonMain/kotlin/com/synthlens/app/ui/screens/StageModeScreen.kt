package com.synthlens.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.ui.components.rememberAmbientBrightness
import com.synthlens.app.ui.components.isOutdoorBrightness
import com.synthlens.app.ui.theme.*
import kotlin.math.*

@Composable
fun StageModeScreen(audioEngine: AudioEngine) {
    val analysis by audioEngine.analysis.collectAsState()
    val isRecording by audioEngine.isRecording.collectAsState()
    val lux = rememberAmbientBrightness()
    val isHighContrast = isOutdoorBrightness(lux)

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    val detected = analysis.detectedSynth
    val stemColors = listOf(Color(0xFFBF00FF), Color(0xFFFF00FF), Color(0xFF00FFFF), Color(0xFF39FF14))

    val gridStroke = if (isHighContrast) 2f else 0.5f
    val gridAlpha = if (isHighContrast) 0.12f else 0.02f
    val ringStroke = if (isHighContrast) 3f else 1f
    val coreStroke = if (isHighContrast) 5f else 2f
    val textBoost = if (isHighContrast) 1f else 0f
    val lineAlpha = if (isHighContrast) 0.6f else 0.2f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2
            val cy = h / 2

            val ga = gridAlpha + analysis.amplitude * 0.03f
            for (i in 0..12) {
                val y = h * i / 12
                drawLine(SynthCyan.copy(alpha = ga), Offset(0f, y), Offset(w, y), gridStroke)
            }
            for (i in 0..8) {
                val x = w * i / 8
                drawLine(SynthCyan.copy(alpha = ga * 0.7f), Offset(x, 0f), Offset(x, h), gridStroke)
            }

            val ringCount = 5
            for (i in 0 until ringCount) {
                val ringR = minOf(w, h) * (0.1f + i * 0.06f) * breathe
                val alpha = (0.08f - i * 0.012f + analysis.amplitude * 0.05f).coerceIn(0.02f, 0.15f)
                val boost = if (isHighContrast) 2.5f else 1f
                drawCircle(SynthCyan.copy(alpha = (alpha * boost).coerceIn(0f, 0.4f)), ringR, Offset(cx, cy), style = Stroke(ringStroke + analysis.amplitude * 2f))
            }

            val coreR = minOf(w, h) * 0.04f * pulse
            val coreGlow = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.15f + analysis.amplitude * 0.2f),
                    SynthCyan.copy(alpha = 0.1f),
                    Color.Transparent
                ),
                center = Offset(cx, cy), radius = coreR * 3f
            )
            drawCircle(coreGlow, coreR * 3f, Offset(cx, cy))
            val coreAlpha = if (isHighContrast) 0.7f else 0.3f
            drawCircle(SynthCyan.copy(alpha = coreAlpha + analysis.amplitude * 0.3f), coreR, Offset(cx, cy))

            analysis.stemProfiles.forEachIndexed { idx, stem ->
                val color = stemColors.getOrElse(idx) { SynthAmber }
                val weight = (stem.energy * 4f).coerceIn(0.05f, 1f)
                val angle = (idx * 90f - 90f) * PI.toFloat() / 180f
                val dist = minOf(w, h) * 0.2f
                val sx = cx + cos(angle) * dist
                val sy = cy + sin(angle) * dist
                val barLen = minOf(w, h) * 0.12f * weight * (0.5f + analysis.amplitude * 0.5f)

                drawLine(color.copy(alpha = lineAlpha), Offset(cx, cy), Offset(sx, sy), coreStroke)
                val orbAlpha = if (isHighContrast) 0.6f else 0.15f
                drawCircle(color.copy(alpha = orbAlpha), barLen * 0.5f, Offset(sx, sy))
                drawCircle(color.copy(alpha = (0.5f * weight * (if (isHighContrast) 2f else 1f)).coerceIn(0f, 1f)), barLen * 0.25f, Offset(sx, sy))
                drawCircle(Color.White.copy(alpha = (0.3f * weight * (if (isHighContrast) 2f else 1f)).coerceIn(0f, 1f)), barLen * 0.1f, Offset(sx, sy))
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            if (isHighContrast) {
                Text(
                    "OUTDOOR MODE",
                    color = SynthAmber.copy(alpha = 0.9f),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                "STAGE_MODE",
                color = SynthCyan.copy(alpha = (0.5f + textBoost * 0.5f).coerceIn(0f, 1f)),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            if (detected != null) {
                Text(
                    detected.brand.uppercase(),
                    color = SynthCyan.copy(alpha = (0.4f + textBoost * 0.6f).coerceIn(0f, 1f)),
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 6.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    detected.name.uppercase(),
                    color = Color.White,
                    fontSize = if (isHighContrast) 48.sp else 42.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Thin
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    StageBigValue("Hz", "${analysis.frequency.toInt()}", isHighContrast)
                    StageBigValue("Wave", analysis.waveformType, isHighContrast)
                    StageBigValue("Oct", "${analysis.octaves}", isHighContrast)
                }

                Spacer(modifier = Modifier.height(30.dp))

                Text(
                    "${(detected.confidence * 100).toInt()}% CONFIDENCE",
                    color = SynthGreen.copy(alpha = if (isHighContrast) 1f else 0.6f),
                    fontSize = if (isHighContrast) 18.sp else 14.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
            } else {
                Text(
                    "LISTENING",
                    color = SynthCyan.copy(alpha = (0.3f + analysis.amplitude * 0.4f + textBoost * 0.3f).coerceIn(0f, 1f)),
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 8.sp,
                    fontWeight = FontWeight.Thin
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                analysis.stemProfiles.forEachIndexed { idx, stem ->
                    val color = stemColors.getOrElse(idx) { SynthAmber }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            stem.stemName,
                            color = color.copy(alpha = if (isHighContrast) 1f else 0.6f),
                            fontSize = if (isHighContrast) 10.sp else 8.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            "${(stem.confidence * 100).toInt()}%",
                            color = color,
                            fontSize = if (isHighContrast) 20.sp else 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StageValue("RMS", "${String.format("%.1f", analysis.rmsLevel)} dB", isHighContrast)
                StageValue("Peak", "${String.format("%.1f", analysis.peakLevel)} dB", isHighContrast)
                StageValue("THD", "${String.format("%.1f", analysis.thd * 100)}%", isHighContrast)
            }
        }
    }
}

@Composable
private fun StageBigValue(label: String, value: String, highContrast: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = DarkOnSurfaceVariant.copy(alpha = if (highContrast) 0.8f else 0.4f),
            fontSize = if (highContrast) 11.sp else 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            value,
            color = Color.White,
            fontSize = if (highContrast) 26.sp else 22.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun StageValue(label: String, value: String, highContrast: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            color = DarkOnSurfaceVariant.copy(alpha = if (highContrast) 0.7f else 0.3f),
            fontSize = if (highContrast) 10.sp else 8.sp,
            fontFamily = FontFamily.Monospace
        )
        Text(
            value,
            color = SynthCyan.copy(alpha = if (highContrast) 1f else 0.7f),
            fontSize = if (highContrast) 16.sp else 12.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}
