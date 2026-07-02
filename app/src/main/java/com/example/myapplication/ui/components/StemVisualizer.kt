package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.engine.StemSynthProfile
import com.example.myapplication.ui.theme.*
import kotlin.math.*

private val NeonPurple = Color(0xFFBF00FF)
private val NeonMagenta = Color(0xFFFF00FF)
private val NeonCyan = Color(0xFF00FFFF)
private val NeonGreen = Color(0xFF39FF14)
private val NeonAmber = Color(0xFFFFD700)
private val NeonPink = Color(0xFFFF1493)
private val NeonBlue = Color(0xFF0080FF)

data class StemVisualState(
    val name: String,
    val color: Color,
    val glowColor: Color,
    val energy: Float,
    val frequency: Float,
    val confidence: Float,
    val waveformType: String,
    val isDominant: Boolean
)

@Composable
fun StemSeparationVisualizer(
    stemProfiles: List<StemSynthProfile>,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val stemColors = listOf(NeonPurple, NeonMagenta, NeonCyan, NeonGreen)
    val glowColors = listOf(
        NeonPurple.copy(alpha = 0.4f),
        NeonMagenta.copy(alpha = 0.4f),
        NeonCyan.copy(alpha = 0.4f),
        NeonGreen.copy(alpha = 0.4f)
    )

    val visualStates = remember(stemProfiles, amplitude) {
        stemProfiles.mapIndexed { index, profile ->
            StemVisualState(
                name = profile.stemName,
                color = stemColors.getOrElse(index) { NeonAmber },
                glowColor = glowColors.getOrElse(index) { NeonAmber.copy(alpha = 0.4f) },
                energy = profile.energy,
                frequency = profile.peakFrequency,
                confidence = profile.confidence,
                waveformType = profile.waveformType,
                isDominant = profile.energy > 0.1f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stemVis")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "animProgress"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulse"
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)),
        label = "scanLine"
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    val smoothAmp = remember { mutableFloatStateOf(0f) }
    LaunchedEffect(amplitude) {
        smoothAmp.floatValue += (amplitude - smoothAmp.floatValue) * 0.35f
    }

    Canvas(modifier = modifier.background(Color.Transparent)) {
        val w = size.width
        val h = size.height
        val cx = w / 2
        val cy = h / 2

        drawNeonGrid(w, h, animProgress)
        drawScanBeam(w, h, scanLine, smoothAmp.floatValue)

        if (visualStates.isEmpty()) {
            drawIdleNeon(cx, cy, w, h, animProgress, breathe)
            return@Canvas
        }

        val totalE = visualStates.sumOf { it.energy.toDouble() }.toFloat().coerceAtLeast(0.001f)

        drawNeonWaves(visualStates, totalE, cx, cy, w, h, smoothAmp.floatValue, animProgress, pulse)
        drawNeonCore(cx, cy, w, smoothAmp.floatValue, pulse, breathe, animProgress)
        drawPulseRings(cx, cy, w, smoothAmp.floatValue, animProgress)
        drawNeonLabels(visualStates, totalE, w, h, smoothAmp.floatValue)
        drawNeonConnections(visualStates, totalE, cx, cy, w, h, animProgress)
        drawParticles(visualStates, totalE, cx, cy, w, h, smoothAmp.floatValue, animProgress)
    }
}

private fun DrawScope.drawNeonGrid(w: Float, h: Float, progress: Float) {
    for (i in 0..8) {
        val y = h * i / 8
        drawLine(NeonCyan.copy(alpha = 0.04f + progress * 0.02f), Offset(0f, y), Offset(w, y), 0.5f)
    }
    for (i in 0..12) {
        val x = w * i / 12
        drawLine(NeonCyan.copy(alpha = 0.03f), Offset(x, 0f), Offset(x, h), 0.5f)
    }
}

private fun DrawScope.drawScanBeam(w: Float, h: Float, progress: Float, amplitude: Float) {
    val scanX = progress * w
    val beamWidth = 80f + amplitude * 40f

    val beamGradient = Brush.horizontalGradient(
        colors = listOf(
            Color.Transparent,
            NeonCyan.copy(alpha = 0.03f + amplitude * 0.04f),
            NeonMagenta.copy(alpha = 0.05f + amplitude * 0.03f),
            Color.Transparent
        ),
        startX = scanX - beamWidth, endX = scanX + beamWidth
    )
    drawRect(brush = beamGradient, topLeft = Offset(scanX - beamWidth, 0f), size = Size(beamWidth * 2, h))

    drawLine(NeonCyan.copy(alpha = 0.15f + amplitude * 0.1f), Offset(scanX, 0f), Offset(scanX, h), 1.5f)
}

private fun DrawScope.drawIdleNeon(cx: Float, cy: Float, w: Float, h: Float, progress: Float, breathe: Float) {
    val baseR = minOf(w, h) * 0.12f * breathe

    for (i in 4 downTo 0) {
        val ringR = baseR + i * 25f + sin(progress * PI.toFloat() * 2 + i) * 10f
        drawCircle(NeonCyan.copy(alpha = 0.08f - i * 0.015f), ringR, Offset(cx, cy))
    }

    val bg = Brush.radialGradient(
        colors = listOf(NeonCyan.copy(alpha = 0.12f), NeonMagenta.copy(alpha = 0.06f), Color.Transparent),
        center = Offset(cx, cy), radius = baseR * 2f
    )
    drawCircle(bg, baseR * 2f, Offset(cx, cy))
    drawCircle(NeonCyan.copy(alpha = 0.2f), baseR, Offset(cx, cy), style = Stroke(1.5f))
    drawCircle(NeonMagenta.copy(alpha = 0.1f), baseR * 1.3f, Offset(cx, cy), style = Stroke(0.8f))

    for (i in 0..6) {
        val angle = (i * 60f + progress * 360f) * PI.toFloat() / 180f
        val pr = baseR * 1.5f
        drawCircle(NeonCyan.copy(alpha = 0.4f), 2f, Offset(cx + cos(angle) * pr, cy + sin(angle) * pr))
    }
}

private fun DrawScope.drawNeonWaves(
    states: List<StemVisualState>, totalE: Float,
    cx: Float, cy: Float, w: Float, h: Float,
    amplitude: Float, progress: Float, pulse: Float
) {
    val waveH = h * 0.18f

    states.forEachIndexed { idx, state ->
        val weight = state.energy / totalE
        val alpha = (weight * 0.8f * (0.4f + amplitude * 0.6f)).coerceIn(0.08f, 0.9f)
        val yOffset = cy + (idx - states.size / 2f) * (h * 0.13f)
        val step = w / 120

        val path = Path()
        val glowPath = Path()

        for (i in 0..120) {
            val x = i * step
            val f1 = (state.frequency / 400f).coerceIn(0.5f, 5f)
            val f2 = (state.frequency / 150f).coerceIn(1f, 7f)
            val y1 = sin((x / w * f1 * PI * 2 + progress * PI * 2).toFloat()) * waveH * weight * amplitude * pulse
            val y2 = sin((x / w * f2 * PI * 2 + progress * PI * 3 + idx * 1.2f).toFloat()) * waveH * weight * amplitude * 0.25f
            val y3 = sin((x / w * 8f + progress * PI * 4).toFloat()) * waveH * weight * amplitude * 0.08f
            val y = yOffset + y1 + y2 + y3

            if (i == 0) { path.moveTo(x, y); glowPath.moveTo(x, y) }
            else {
                val prevX = (i - 1) * step
                val pF1 = (state.frequency / 400f).coerceIn(0.5f, 5f)
                val pY1 = sin((prevX / w * pF1 * PI * 2 + progress * PI * 2).toFloat()) * waveH * weight * amplitude * pulse
                val pY2 = sin((prevX / w * f2 * PI * 2 + progress * PI * 3 + idx * 1.2f).toFloat()) * waveH * weight * amplitude * 0.25f
                val pY3 = sin((prevX / w * 8f + progress * PI * 4).toFloat()) * waveH * weight * amplitude * 0.08f
                val pY = yOffset + pY1 + pY2 + pY3
                val cpx = (prevX + x) / 2f
                path.cubicTo(cpx, pY, cpx, y, x, y)
                glowPath.cubicTo(cpx, pY, cpx, y, x, y)
            }
        }

        // Outer glow (wide, very dim)
        drawPath(glowPath, state.glowColor.copy(alpha = alpha * 0.15f), style = Stroke(24f, cap = StrokeCap.Round))
        // Mid glow
        drawPath(path, state.color.copy(alpha = alpha * 0.3f), style = Stroke(8f, cap = StrokeCap.Round))
        // Inner glow
        drawPath(path, state.color.copy(alpha = alpha * 0.6f), style = Stroke(3f, cap = StrokeCap.Round))
        // Core line (bright neon)
        drawPath(path, state.color.copy(alpha = alpha), style = Stroke(1.5f, cap = StrokeCap.Round))
        // White hot center
        drawPath(path, Color.White.copy(alpha = alpha * 0.4f), style = Stroke(0.8f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawNeonCore(cx: Float, cy: Float, w: Float, amplitude: Float, pulse: Float, breathe: Float, progress: Float) {
    val coreR = minOf(w, size.height) * 0.06f * (1f + amplitude * 0.4f * pulse)

    // Multi-layer neon glow
    val outerGlow = Brush.radialGradient(
        colors = listOf(NeonCyan.copy(alpha = 0.15f), NeonMagenta.copy(alpha = 0.08f), Color.Transparent),
        center = Offset(cx, cy), radius = coreR * 3f
    )
    drawCircle(outerGlow, coreR * 3f, Offset(cx, cy))

    val midGlow = Brush.radialGradient(
        colors = listOf(NeonCyan.copy(alpha = 0.25f), NeonPurple.copy(alpha = 0.12f), Color.Transparent),
        center = Offset(cx, cy), radius = coreR * 2f
    )
    drawCircle(midGlow, coreR * 2f, Offset(cx, cy))

    // Neon rings
    drawCircle(NeonCyan.copy(alpha = 0.3f + amplitude * 0.2f), coreR * 1.8f, Offset(cx, cy), style = Stroke(1.5f))
    drawCircle(NeonMagenta.copy(alpha = 0.2f + amplitude * 0.15f), coreR * 1.4f, Offset(cx, cy), style = Stroke(1f))

    // Core fill
    val coreGradient = Brush.radialGradient(
        colors = listOf(Color.White.copy(alpha = 0.3f + amplitude * 0.2f), NeonCyan.copy(alpha = 0.4f), NeonMagenta.copy(alpha = 0.2f)),
        center = Offset(cx, cy), radius = coreR
    )
    drawCircle(coreGradient, coreR, Offset(cx, cy))

    // Orbiting neon particles
    for (i in 0..16) {
        val angle = (i * 22.5f + progress * 360f + amplitude * 120f) * PI.toFloat() / 180f
        val orbitR = coreR * (1.6f + amplitude * 1.2f * abs(sin(angle * 2 + progress * 3)))
        val px = cx + cos(angle) * orbitR
        val py = cy + sin(angle) * orbitR
        val dotSize = 1.5f + amplitude * 4f * abs(sin(angle * 3))
        val dotAlpha = (0.3f + amplitude * 0.5f * abs(sin(angle * 2))).coerceIn(0.15f, 0.8f)

        val dotColor = when (i % 4) {
            0 -> NeonCyan; 1 -> NeonMagenta; 2 -> NeonGreen; else -> NeonPurple
        }
        // Glow around particle
        drawCircle(dotColor.copy(alpha = dotAlpha * 0.3f), dotSize * 3f, Offset(px, py))
        // Particle
        drawCircle(dotColor.copy(alpha = dotAlpha), dotSize, Offset(px, py))
        // Hot center
        drawCircle(Color.White.copy(alpha = dotAlpha * 0.5f), dotSize * 0.4f, Offset(px, py))
    }
}

private fun DrawScope.drawPulseRings(cx: Float, cy: Float, w: Float, amplitude: Float, progress: Float) {
    if (amplitude < 0.02f) return

    for (i in 0..3) {
        val phase = (progress * 4f + i * 0.7f) % 1f
        val ringR = minOf(w, size.height) * 0.05f + phase * minOf(w, size.height) * 0.4f
        val alpha = (1f - phase) * amplitude * 0.3f

        drawCircle(NeonCyan.copy(alpha = alpha), ringR, Offset(cx, cy), style = Stroke(1f + amplitude * 2f))
    }
}

private fun DrawScope.drawNeonLabels(
    states: List<StemVisualState>, totalE: Float, w: Float, h: Float, amplitude: Float
) {
    val labelX = w * 0.06f
    val startY = h * 0.1f
    val spacing = h * 0.2f

    states.forEachIndexed { idx, state ->
        val y = startY + idx * spacing
        val weight = state.energy / totalE
        val barW = (w * 0.18f * weight * 5f).coerceIn(w * 0.02f, w * 0.4f)
        val barAlpha = (0.3f + weight * 0.5f + amplitude * 0.2f).coerceIn(0.2f, 1f)

        // Glow behind bar
        drawRoundRect(state.glowColor.copy(alpha = barAlpha * 0.15f), Offset(labelX - 4f, y - 4f), Size(barW + 8f, 12f), CornerRadius(4f))
        // Bar
        drawRoundRect(state.color.copy(alpha = barAlpha), Offset(labelX, y), Size(barW, 4f), CornerRadius(2f))
        // Hot edge
        drawRoundRect(Color.White.copy(alpha = barAlpha * 0.5f), Offset(labelX, y), Size(barW.coerceAtMost(3f), 4f), CornerRadius(2f))

        // Neon dot
        drawCircle(state.glowColor.copy(alpha = 0.5f), 6f, Offset(labelX - 10f, y + 2f))
        drawCircle(state.color.copy(alpha = if (state.isDominant) 0.9f else 0.5f), 3f, Offset(labelX - 10f, y + 2f))
        drawCircle(Color.White.copy(alpha = 0.3f), 1.5f, Offset(labelX - 10f, y + 2f))
    }
}

private fun DrawScope.drawNeonConnections(
    states: List<StemVisualState>, totalE: Float,
    cx: Float, cy: Float, w: Float, h: Float, progress: Float
) {
    val startX = w * 0.22f
    val endX = w * 0.78f
    val startY = h * 0.1f
    val spacing = h * 0.2f

    states.forEachIndexed { idx, state ->
        val weight = state.energy / totalE
        val y = startY + idx * spacing
        val cpX = cx + sin(progress * PI.toFloat() * 2 + idx * 1.5f) * w * 0.08f

        val path = Path()
        path.moveTo(startX, y)
        path.quadraticTo(cpX, y + (cy - y) * 0.3f, endX, cy)

        // Glow layer
        drawPath(path, state.glowColor.copy(alpha = weight * 0.15f), style = Stroke(4f, cap = StrokeCap.Round))
        // Core line
        drawPath(path, state.color.copy(alpha = weight * 0.35f * (0.5f + progress * 0.5f)), style = Stroke(1f, cap = StrokeCap.Round))
    }
}

private fun DrawScope.drawParticles(
    states: List<StemVisualState>, totalE: Float,
    cx: Float, cy: Float, w: Float, h: Float, amplitude: Float, progress: Float
) {
    if (amplitude < 0.01f) return

    val particleCount = (amplitude * 30f).toInt().coerceIn(2, 25)

    for (i in 0 until particleCount) {
        val seed = i * 137.5f
        val angle = (seed + progress * 180f + amplitude * 60f) * PI.toFloat() / 180f
        val dist = minOf(w, h) * (0.05f + (sin(seed * 0.01f + progress * 2f) * 0.5f + 0.5f) * 0.4f) * (1f + amplitude * 0.5f)
        val px = cx + cos(angle) * dist
        val py = cy + sin(angle) * dist

        val stateIdx = i % states.size
        val state = states[stateIdx]
        val size = 1f + amplitude * 3f * abs(sin(seed * 0.03f))
        val alpha = (0.2f + amplitude * 0.4f * abs(sin(seed * 0.02f + progress))).coerceIn(0.1f, 0.7f)

        // Glow
        drawCircle(state.glowColor.copy(alpha = alpha * 0.3f), size * 3f, Offset(px, py))
        // Particle
        drawCircle(state.color.copy(alpha = alpha), size, Offset(px, py))
        // Hot center
        drawCircle(Color.White.copy(alpha = alpha * 0.4f), size * 0.3f, Offset(px, py))
    }
}

@Composable
fun StemProgressIndicator(
    stemName: String, color: Color, energy: Float, confidence: Float, modifier: Modifier = Modifier
) {
    val safeEnergy = if (energy.isFinite()) energy.coerceIn(0f, 1f) else 0f
    val animEnergy by animateFloatAsState(targetValue = safeEnergy, animationSpec = tween(300, easing = FastOutSlowInEasing), label = "energy")
    val glowColor = color.copy(alpha = 0.3f)

    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        // Neon dot with glow
        Box(contentAlignment = Alignment.Center) {
            Box(Modifier.size(10.dp).clip(CircleShape).background(glowColor))
            Box(Modifier.size(5.dp).clip(CircleShape).background(color))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stemName, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text("${(confidence * 100).toInt()}%", color = DarkOnSurfaceVariant.copy(alpha = 0.7f), fontSize = 9.sp)
            }
            Spacer(modifier = Modifier.height(3.dp))
            Canvas(Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp))) {
                // Background
                drawRoundRect(color.copy(alpha = 0.08f), Offset.Zero, Size(size.width, size.height), CornerRadius(2f))
                // Fill with glow
                val fillW = size.width * animEnergy.coerceIn(0f, 1f)
                drawRoundRect(glowColor, Offset.Zero, Size(fillW + 6f, size.height), CornerRadius(2f))
                drawRoundRect(color.copy(alpha = 0.7f), Offset.Zero, Size(fillW, size.height), CornerRadius(2f))
                // Hot edge
                if (fillW > 2f) drawRoundRect(Color.White.copy(alpha = 0.4f), Offset.Zero, Size(2f, size.height), CornerRadius(1f))
            }
        }
    }
}
