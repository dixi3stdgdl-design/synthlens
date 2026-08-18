package com.synthlens.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.synthlens.app.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

@Composable
fun WaveformDisplay(
    waveformPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = WaveformGreen,
    glowColor: Color = SynthCyan,
    backgroundColor: Color = DarkSurface
) {
    val infiniteTransition = rememberInfiniteTransition()
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )

    Canvas(modifier = modifier.background(backgroundColor)) {
        if (waveformPoints.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val centerY = height / 2

        for (i in 0..4) {
            val y = height * i / 4
            drawLine(color = DarkBorder.copy(alpha = 0.3f), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 0.5f)
        }
        for (i in 0..8) {
            val x = width * i / 8
            drawLine(color = DarkBorder.copy(alpha = 0.3f), start = Offset(x, 0f), end = Offset(x, height), strokeWidth = 0.5f)
        }

        drawLine(color = DarkBorder, start = Offset(0f, centerY), end = Offset(width, centerY), strokeWidth = 1f)

        val scanX = scanLine * width
        val scanGradient = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                glowColor.copy(alpha = 0.08f),
                Color.Transparent
            ),
            startX = scanX - 60f,
            endX = scanX + 60f
        )
        drawRect(brush = scanGradient, topLeft = Offset(scanX - 60f, 0f), size = Size(120f, height))

        val path = Path()
        val fillPath = Path()
        val step = width / (waveformPoints.size - 1).coerceAtLeast(1)
        val maxAmp = height * 0.4f

        waveformPoints.forEachIndexed { index, value ->
            val x = index * step
            val y = centerY - (value * maxAmp)
            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, centerY)
                fillPath.lineTo(x, y)
            } else {
                val prevX = (index - 1) * step
                val prevY = centerY - (waveformPoints[index - 1] * maxAmp)
                val cpx = (prevX + x) / 2f
                path.cubicTo(cpx, prevY, cpx, y, x, y)
                fillPath.cubicTo(cpx, prevY, cpx, y, x, y)
            }
        }
        fillPath.lineTo(width, centerY)
        fillPath.close()

        val fillBrush = Brush.verticalGradient(
            colors = listOf(
                lineColor.copy(alpha = 0.15f),
                lineColor.copy(alpha = 0.02f),
                Color.Transparent
            ),
            startY = centerY - maxAmp,
            endY = centerY
        )
        drawPath(path = fillPath, brush = fillBrush)

        drawPath(
            path = path,
            color = glowColor.copy(alpha = glowAlpha * 0.12f),
            style = Stroke(width = 20f, cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = glowColor.copy(alpha = glowAlpha * 0.25f),
            style = Stroke(width = 10f, cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = glowColor.copy(alpha = glowAlpha * 0.5f),
            style = Stroke(width = 5f, cap = StrokeCap.Round)
        )
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun SpectrumBars(
    spectrumData: FloatArray,
    modifier: Modifier = Modifier,
    barColor: Color = SynthCyan,
    peakColor: Color = SynthMagenta,
    backgroundColor: Color = DarkSurface
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )

    Canvas(modifier = modifier.background(backgroundColor)) {
        if (spectrumData.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val barCount = 64
        val binsPerBar = spectrumData.size / barCount
        val barWidth = width / barCount * 0.65f
        val gap = width / barCount * 0.35f

        for (i in 0..4) {
            val y = height * i / 4
            drawLine(color = DarkBorder.copy(alpha = 0.2f), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 0.5f)
        }

        for (i in 0 until barCount) {
            val startBin = i * binsPerBar
            val endBin = minOf(startBin + binsPerBar, spectrumData.size)

            var sum = 0f
            var maxVal = 0f
            for (j in startBin until endBin) {
                if (j < spectrumData.size) {
                    sum += spectrumData[j]
                    if (spectrumData[j] > maxVal) maxVal = spectrumData[j]
                }
            }
            val avg = sum / binsPerBar

            val barHeight = (avg * height * 2.5f * pulse).coerceIn(0f, height * 0.95f)
            val x = i * (barWidth + gap)
            val progress = i.toFloat() / barCount

            val r = (barColor.red + (peakColor.red - barColor.red) * progress)
            val g = (barColor.green + (peakColor.green - barColor.green) * progress)
            val b = (barColor.blue + (peakColor.blue - barColor.blue) * progress)
            val color = Color(r, g, b)

            val glowAlphaVal = (avg * 0.4f).coerceIn(0f, 0.3f)
            drawRect(
                color = color.copy(alpha = glowAlphaVal),
                topLeft = Offset(x - 3f, height - barHeight - 6f),
                size = Size(barWidth + 6f, barHeight + 8f)
            )

            val gradient = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.95f),
                    color.copy(alpha = 0.7f),
                    color.copy(alpha = 0.4f)
                ),
                startY = height - barHeight,
                endY = height
            )
            drawRect(
                brush = gradient,
                topLeft = Offset(x, height - barHeight),
                size = Size(barWidth, barHeight)
            )

            if (barHeight > 4f) {
                val peakY = height - barHeight - 3f
                val peakShimmer = sin(shimmer * PI.toFloat() * 2 + i * 0.3f) * 0.3f + 0.7f
                drawRect(
                    color = Color.White.copy(alpha = 0.6f * peakShimmer),
                    topLeft = Offset(x, peakY),
                    size = Size(barWidth, 2f)
                )
            }
        }
    }
}

@Composable
fun SpectrogramWaterfall(
    spectrumHistory: List<FloatArray>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkBackground
) {
    val infiniteTransition = rememberInfiniteTransition()
    val timeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        )
    )

    Canvas(modifier = modifier.background(backgroundColor)) {
        if (spectrumHistory.isEmpty()) return@Canvas

        val width = size.width
        val height = size.height
        val rows = spectrumHistory.size
        val rowHeight = height / rows

        for (i in 0..4) {
            val y = height * i / 4
            drawLine(color = DarkBorder.copy(alpha = 0.15f), start = Offset(0f, y), end = Offset(width, y), strokeWidth = 0.5f)
        }

        spectrumHistory.forEachIndexed { rowIndex, spectrum ->
            val y = rowIndex * rowHeight
            val binsPerColumn = spectrum.size / 64
            val ageFactor = 1f - (rowIndex.toFloat() / rows)

            for (col in 0 until 64) {
                val binStart = col * binsPerColumn
                val binEnd = minOf(binStart + binsPerColumn, spectrum.size)
                var sum = 0f
                for (b in binStart until binEnd) {
                    if (b < spectrum.size) sum += spectrum[b]
                }
                val value = (sum / binsPerColumn * ageFactor).coerceIn(0f, 1f)
                val x = col * (width / 64)

                val color = when {
                    value > 0.7f -> Color(0xFFFF1744)
                    value > 0.5f -> Color(0xFFFF9100)
                    value > 0.3f -> SynthAmber
                    value > 0.15f -> SynthCyan
                    value > 0.05f -> SynthCyan.copy(alpha = 0.5f)
                    else -> Color.Transparent
                }

                if (value > 0.05f) {
                    drawRect(
                        color = color.copy(alpha = (value * 0.85f).coerceAtLeast(0.05f)),
                        topLeft = Offset(x, y),
                        size = Size(width / 64, rowHeight + 1f)
                    )
                }
            }
        }
    }
}

@Composable
fun SpeakerVisualization(
    amplitude: Float,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2
        val cy = size.height / 2
        val baseR = size.width * 0.32f

        if (isRecording) {
            for (i in 5 downTo 0) {
                val ringR = baseR + (i * 28f * amplitude * pulse)
                val a = (0.08f - i * 0.012f).coerceAtLeast(0.01f)
                drawCircle(
                    color = SynthCyan.copy(alpha = a),
                    radius = ringR,
                    center = Offset(cx, cy)
                )
            }
        }

        val bgGradient = Brush.radialGradient(
            colors = listOf(
                DarkSurfaceVariant.copy(alpha = 0.9f),
                DarkCard.copy(alpha = 0.95f),
                DarkBackground
            ),
            center = Offset(cx, cy),
            radius = baseR
        )
        drawCircle(brush = bgGradient, radius = baseR, center = Offset(cx, cy))

        drawCircle(color = DarkBorder, radius = baseR * 0.92f, center = Offset(cx, cy), style = Stroke(width = 1.5f))

        drawCircle(color = DarkBorder.copy(alpha = 0.5f), radius = baseR * 0.7f, center = Offset(cx, cy), style = Stroke(width = 1f))
        drawCircle(color = DarkBorder.copy(alpha = 0.3f), radius = baseR * 0.5f, center = Offset(cx, cy), style = Stroke(width = 0.5f))

        val coneGradient = Brush.radialGradient(
            colors = listOf(
                SynthCyan.copy(alpha = if (isRecording) 0.15f else 0.05f),
                SynthCyan.copy(alpha = if (isRecording) 0.08f else 0.02f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = baseR * 0.65f
        )
        drawCircle(brush = coneGradient, radius = baseR * 0.65f, center = Offset(cx, cy))

        drawCircle(color = DarkSurface, radius = baseR * 0.28f, center = Offset(cx, cy))

        if (isRecording && amplitude > 0.02f) {
            for (i in 0..48) {
                val angle = (i * 7.5f + waveOffset) * PI.toFloat() / 180f
                val waveR = baseR * (0.82f + amplitude * 0.35f * sin(angle * 4 + waveOffset * 0.08f))
                val px = cx + cos(angle) * waveR
                val py = cy + sin(angle) * waveR
                val dotSize = 1.5f + amplitude * 5f * abs(sin(angle * 6 + waveOffset * 0.05f))
                val dotAlpha = (0.3f + amplitude * 0.5f * abs(sin(angle * 3))).coerceIn(0.1f, 0.8f)

                drawCircle(
                    color = WaveformGreen.copy(alpha = dotAlpha),
                    radius = dotSize,
                    center = Offset(px, py)
                )
            }

            val glowR = baseR * (0.7f + amplitude * 0.2f * pulse)
            val innerGlow = Brush.radialGradient(
                colors = listOf(
                    SynthCyan.copy(alpha = 0.06f * amplitude),
                    Color.Transparent
                ),
                center = Offset(cx, cy),
                radius = glowR
            )
            drawCircle(brush = innerGlow, radius = glowR, center = Offset(cx, cy))
        }
    }
}

@Composable
fun TerrainWaterfall(
    spectrumHistory: List<FloatArray>,
    amplitude: Float,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkBackground
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scrollOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing)
        )
    )
    val reactivePulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Canvas(modifier = modifier.background(backgroundColor)) {
        if (spectrumHistory.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val rows = spectrumHistory.size.coerceAtMost(28)
        val cols = 48

        val perspectiveSkew = 0.55f
        val rowDepth = h * 0.75f / rows
        val rowHeightScale = 0.35f

        for (row in 0 until rows) {
            val spectrum = spectrumHistory[spectrumHistory.size - 1 - row]
            val depthFraction = row.toFloat() / rows
            val yBase = h * 0.15f + row * rowDepth * perspectiveSkew
            val verticalScale = 1f - depthFraction * 0.3f
            val alphaFade = (1f - depthFraction * 0.7f).coerceIn(0.08f, 1f)

            val binsPerCol = spectrum.size / cols

            val greenComponent = (0.9f - depthFraction * 0.4f).coerceIn(0.3f, 0.9f)
            val redComponent = (0.1f + depthFraction * 0.6f).coerceIn(0.1f, 0.7f)

            val linePath = Path()
            val fillPath = Path()
            var firstX = 0f
            var firstY = 0f

            for (col in 0..cols) {
                val colFraction = col.toFloat() / cols
                val binStart = (colFraction * spectrum.size).toInt().coerceIn(0, spectrum.size - 1)
                val binEnd = ((colFraction + 1f / cols) * spectrum.size).toInt()
                    .coerceIn(binStart, spectrum.size)

                var sum = 0f
                var count = 0
                for (b in binStart until binEnd) {
                    if (b < spectrum.size) {
                        sum += spectrum[b]
                        count++
                    }
                }
                val value = if (count > 0) sum / count else 0f

                val x = colFraction * w
                val elevation = value * h * rowHeightScale * verticalScale * reactivePulse
                val y = yBase - elevation

                if (col == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, yBase)
                    fillPath.lineTo(x, y)
                    firstX = x
                    firstY = y
                } else {
                    val prevFrac = (col - 1).toFloat() / cols
                    val prevBinStart = (prevFrac * spectrum.size).toInt().coerceIn(0, spectrum.size - 1)
                    val prevBinEnd = ((prevFrac + 1f / cols) * spectrum.size).toInt()
                        .coerceIn(prevBinStart, spectrum.size)
                    var prevSum = 0f
                    var prevCount = 0
                    for (b in prevBinStart until prevBinEnd) {
                        if (b < spectrum.size) {
                            prevSum += spectrum[b]
                            prevCount++
                        }
                    }
                    val prevValue = if (prevCount > 0) prevSum / prevCount else 0f
                    val prevX = prevFrac * w
                    val prevElevation = prevValue * h * rowHeightScale * verticalScale * reactivePulse
                    val prevY = yBase - prevElevation

                    val cpx = (prevX + x) / 2f
                    linePath.cubicTo(cpx, prevY, cpx, y, x, y)
                    fillPath.cubicTo(cpx, prevY, cpx, y, x, y)
                }
            }

            fillPath.lineTo(w, yBase)
            fillPath.close()

            val fillGrad = Brush.verticalGradient(
                colors = listOf(
                    Color(redComponent, greenComponent, 0.2f, alpha = alphaFade * 0.12f),
                    Color(redComponent, greenComponent * 0.6f, 0.1f, alpha = alphaFade * 0.03f),
                    Color.Transparent
                ),
                startY = yBase - h * rowHeightScale * verticalScale,
                endY = yBase
            )
            drawPath(fillPath, fillGrad)

            val lineColor = Color(redComponent, greenComponent, 0.2f)
            drawPath(
                linePath,
                lineColor.copy(alpha = alphaFade * 0.08f),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
            drawPath(
                linePath,
                lineColor.copy(alpha = alphaFade * 0.25f),
                style = Stroke(width = 3.5f, cap = StrokeCap.Round)
            )
            drawPath(
                linePath,
                lineColor.copy(alpha = alphaFade * 0.7f),
                style = Stroke(width = 1.2f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }

        for (i in 0..6) {
            val x = w * i / 6
            drawLine(
                DarkBorder.copy(alpha = 0.12f),
                Offset(x, h * 0.1f),
                Offset(x, h),
                strokeWidth = 0.5f
            )
        }
        for (i in 0..4) {
            val y = h * (0.15f + i * 0.2f)
            drawLine(
                DarkBorder.copy(alpha = 0.1f),
                Offset(0f, y),
                Offset(w, y),
                strokeWidth = 0.5f
            )
        }
    }
}

@Composable
fun RadialOrbitalSphere(
    spectrumData: FloatArray,
    amplitude: Float,
    harmonicProfile: List<Float>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = DarkBackground
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing)
        )
    )
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val innerPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        )
    )
    val reactiveGlow = rememberReactiveAmplitude(amplitude, 0.4f)

    Canvas(modifier = modifier.background(backgroundColor)) {
        val cx = size.width / 2
        val cy = size.height / 2
        val maxR = size.width * 0.42f

        for (r in 0..6) {
            val ringR = maxR * (0.15f + r * 0.13f) * breathe
            drawCircle(
                DarkBorder.copy(alpha = 0.06f + r * 0.01f),
                ringR,
                Offset(cx, cy),
                style = Stroke(width = 0.5f)
            )
        }

        val ringCount = 12
        for (ring in 0 until ringCount) {
            val ringFraction = ring.toFloat() / ringCount
            val baseRadius = maxR * (0.2f + ringFraction * 0.75f) * breathe
            val segments = 120
            val ringAlpha = (0.15f + (1f - ringFraction) * 0.55f).coerceIn(0.1f, 0.7f)
            val ringRotation = rotation + ring * 7f + ringFraction * 30f

            val ringColor = when {
                ringFraction < 0.3f -> Color(0.1f, 0.95f, 0.46f)
                ringFraction < 0.6f -> Color(0.3f, 0.85f, 0.35f)
                else -> Color(0.55f + ringFraction * 0.2f, 0.8f - ringFraction * 0.2f, 0.15f)
            }

            val path = Path()
            for (s in 0..segments) {
                val angle = (s.toFloat() / segments * 360f + ringRotation) * PI.toFloat() / 180f

                val specIndex = ((s.toFloat() / segments) * spectrumData.size).toInt()
                    .coerceIn(0, spectrumData.size - 1)
                val specValue = if (spectrumData.isNotEmpty()) spectrumData[specIndex] else 0f

                val harmonicIndex = ((s.toFloat() / segments) * harmonicProfile.size).toInt()
                    .coerceIn(0, (harmonicProfile.size - 1).coerceAtLeast(0))
                val harmonicValue = if (harmonicProfile.isNotEmpty() && harmonicIndex < harmonicProfile.size)
                    harmonicProfile[harmonicIndex] else 0f

                val modulation = 1f + specValue * 0.4f * breathe + harmonicValue * 0.0008f
                val r = baseRadius * modulation

                val tiltAngle = ringFraction * 0.4f
                val tiltedX = cos(angle) * r
                val tiltedY = sin(angle) * r * (0.3f + tiltAngle * 0.7f)

                val px = cx + tiltedX * cos(ringRotation * PI.toFloat() / 720f) -
                        tiltedY * sin(ringRotation * PI.toFloat() / 720f) * 0.3f
                val py = cy + tiltedY * cos(ringRotation * PI.toFloat() / 720f) +
                        tiltedX * sin(ringRotation * PI.toFloat() / 720f) * 0.15f

                if (s == 0) path.moveTo(px, py)
                else path.lineTo(px, py)
            }
            path.close()

            drawPath(
                path,
                ringColor.copy(alpha = ringAlpha * 0.06f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            drawPath(
                path,
                ringColor.copy(alpha = ringAlpha * 0.2f),
                style = Stroke(width = 2f, cap = StrokeCap.Round)
            )
            drawPath(
                path,
                ringColor.copy(alpha = ringAlpha),
                style = Stroke(width = 0.8f, cap = StrokeCap.Round)
            )
        }

        val innerR = maxR * 0.12f * breathe * (1f + amplitude * 0.3f)
        val innerGrad = Brush.radialGradient(
            colors = listOf(
                Color(0.0f, 0.9f + reactiveGlow * 0.1f, 0.45f, alpha = 0.3f + reactiveGlow * 0.2f),
                Color(0.0f, 0.7f, 0.3f, alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = innerR
        )
        drawCircle(innerGrad, innerR, Offset(cx, cy))

        drawCircle(
            Color(0.0f, 0.95f, 0.5f, alpha = 0.6f + reactiveGlow * 0.3f),
            innerR * 0.3f,
            Offset(cx, cy)
        )
        drawCircle(
            Color.White.copy(alpha = 0.4f + reactiveGlow * 0.3f),
            innerR * 0.1f,
            Offset(cx, cy)
        )

        for (i in 0..3) {
            val tickR = maxR * (0.85f + i * 0.05f)
            drawCircle(
                DarkBorder.copy(alpha = 0.15f),
                tickR * breathe,
                Offset(cx, cy),
                style = Stroke(width = 0.5f)
            )
        }

        val dataLabelR = maxR * 1.08f * breathe
        for (angle in listOf(0f, 90f, 180f, 270f)) {
            val rad = (angle + rotation * 0.1f) * PI.toFloat() / 180f
            val lx = cx + cos(rad) * dataLabelR
            val ly = cy + sin(rad) * dataLabelR
            drawCircle(
                SynthCyan.copy(alpha = 0.3f),
                2f,
                Offset(lx, ly)
            )
        }
    }
}

private fun lerp(start: Color, end: Color, fraction: Float): Color {
    return Color(
        red = start.red + (end.red - start.red) * fraction,
        green = start.green + (end.green - start.green) * fraction,
        blue = start.blue + (end.blue - start.blue) * fraction,
        alpha = start.alpha + (end.alpha - start.alpha) * fraction
    )
}
