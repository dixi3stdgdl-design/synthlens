package com.synthlens.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.ui.theme.*
import kotlin.math.*

@Composable
fun SynthPanel(
    modifier: Modifier = Modifier,
    label: String = "",
    alpha: Float = 0.3f,
    glowColor: Color = SynthCyan,
    glowIntensity: Float = 0f,
    content: @Composable () -> Unit
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    val shape = RoundedCornerShape(6.dp)
    val padAlpha by animateFloatAsState(
        targetValue = ((alpha + glowIntensity * 0.08f) * boost).let { v -> if (v.isFinite()) v.coerceIn(0f, 1f) else 0f },
        animationSpec = tween(300), label = "padAlpha"
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = (2 + glowIntensity * 4).dp,
                shape = shape,
                ambientColor = glowColor.copy(alpha = glowIntensity * 0.15f * boost),
                spotColor = glowColor.copy(alpha = glowIntensity * 0.1f * boost)
            )
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E1E2E).copy(alpha = padAlpha.coerceIn(0f, 1f)),
                        Color(0xFF12121E).copy(alpha = (padAlpha * 0.9f).coerceIn(0f, 1f)),
                        Color(0xFF0A0A14).copy(alpha = (padAlpha * 0.7f).coerceIn(0f, 1f))
                    )
                )
            )
            .border(
                width = if (isBright) 1.dp else 0.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        glowColor.copy(alpha = (0.08f + glowIntensity * 0.1f) * boost),
                        Color.White.copy(alpha = 0.02f * boost),
                        Color.Transparent
                    )
                ),
                shape = shape
            ),
        content = { content() }
    )
}

@Composable
fun SynthKnob(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    color: Color = SynthCyan,
    modifier: Modifier = Modifier,
    knobSize: Dp = 64.dp,
    minValue: Float = 0f,
    maxValue: Float = 1f
) {
    val view = LocalView.current
    val normalizedValue = (value - minValue) / (maxValue - minValue)
    val safeNormalized = if (normalizedValue.isFinite()) normalizedValue.coerceIn(0f, 1f) else 0f
    val animatedValue by animateFloatAsState(
        targetValue = safeNormalized,
        animationSpec = tween(120, easing = FastOutSlowInEasing),
        label = "knobAnim"
    )
    var lastTickValue by remember { mutableFloatStateOf(safeNormalized) }
    val tickThreshold = 0.04f

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Canvas(
            modifier = Modifier
                .size(knobSize)
                .clip(CircleShape)
                .shadow(4.dp, CircleShape, ambientColor = Color.Black.copy(alpha = 0.5f))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val sensitivity = 0.005f
                        val delta = -dragAmount.y * sensitivity
                        val newNorm = (safeNormalized + delta).coerceIn(0f, 1f)
                        val newValue = minValue + newNorm * (maxValue - minValue)
                        onValueChange(newValue)
                        if (kotlin.math.abs(newNorm - lastTickValue) >= tickThreshold) {
                            HapticEngine.tick(view)
                            lastTickValue = newNorm
                        }
                    }
                }
        ) {
            val cx = size.width / 2
            val cy = size.height / 2
            val radius = size.width / 2

            drawCircle(Color(0xFF080810), radius, Offset(cx, cy))

            drawCircle(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF2A2A3A),
                        Color(0xFF15151F),
                        Color(0xFF0A0A12)
                    ),
                    center = Offset(cx - radius * 0.15f, cy - radius * 0.15f),
                    radius = radius
                ),
                radius, Offset(cx, cy)
            )

            drawCircle(
                Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.06f),
                        Color.Transparent
                    ),
                    center = Offset(cx - radius * 0.2f, cy - radius * 0.3f),
                    radius = radius * 0.6f
                ),
                radius * 0.8f, Offset(cx, cy)
            )

            val ringRadius = radius * 0.82f
            drawCircle(
                Color.White.copy(alpha = 0.04f),
                ringRadius,
                Offset(cx, cy),
                style = Stroke(1.dp.toPx())
            )

            val startAngle = 135f
            val sweepAngle = 270f

            drawArc(
                color = Color.White.copy(alpha = 0.06f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(cx - ringRadius, cy - ringRadius),
                size = Size(ringRadius * 2, ringRadius * 2),
                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
            )

            val activeSweep = sweepAngle * animatedValue
            drawArc(
                color = color.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = Offset(cx - ringRadius, cy - ringRadius),
                size = Size(ringRadius * 2, ringRadius * 2),
                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = color.copy(alpha = 0.4f),
                startAngle = startAngle,
                sweepAngle = activeSweep,
                useCenter = false,
                topLeft = Offset(cx - ringRadius, cy - ringRadius),
                size = Size(ringRadius * 2, ringRadius * 2),
                style = Stroke(1.dp.toPx(), cap = StrokeCap.Round)
            )

            val indicatorAngle = (startAngle + activeSweep) * PI.toFloat() / 180f
            val indicatorR = ringRadius * 0.7f
            val ix = cx + cos(indicatorAngle) * indicatorR
            val iy = cy + sin(indicatorAngle) * indicatorR

            drawCircle(color.copy(alpha = 0.15f), 5.dp.toPx(), Offset(ix, iy))
            drawCircle(color.copy(alpha = 0.5f), 3.dp.toPx(), Offset(ix, iy))
            drawCircle(Color.White.copy(alpha = 0.3f), 1.5.dp.toPx(), Offset(ix, iy))

            val dotCount = 24
            for (i in 0..dotCount) {
                val dotAngle = (startAngle + (sweepAngle * i / dotCount)) * PI.toFloat() / 180f
                val dotR = ringRadius + 4.dp.toPx()
                val dx = cx + cos(dotAngle) * dotR
                val dy = cy + sin(dotAngle) * dotR
                val dotAlpha = if (i.toFloat() / dotCount <= animatedValue) 0.25f else 0.05f
                drawCircle(color.copy(alpha = dotAlpha), 0.8.dp.toPx(), Offset(dx, dy))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            label,
            color = color.copy(alpha = 0.4f),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
fun SynthSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    color: Color = SynthCyan,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val safeValue = if (value.isFinite()) value.coerceIn(0f, 1f) else 0f
    val animatedValue by animateFloatAsState(
        targetValue = safeValue,
        animationSpec = tween(150), label = "sliderAnim"
    )
    var lastTickValue by remember { mutableFloatStateOf(safeValue) }
    val tickThreshold = 0.06f

    Column(modifier = modifier) {
        Text(
            label,
            color = color.copy(alpha = 0.4f),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .shadow(2.dp, RoundedCornerShape(3.dp), ambientColor = Color.Black.copy(alpha = 0.4f))
                .clip(RoundedCornerShape(3.dp))
                .background(Color(0xFF0E0E18))
                .border(0.5.dp, Color.White.copy(alpha = 0.04f), RoundedCornerShape(3.dp))
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val newValue = (safeValue - dragAmount.y / size.height).coerceIn(0f, 1f)
                        onValueChange(newValue)
                        if (kotlin.math.abs(newValue - lastTickValue) >= tickThreshold) {
                            HapticEngine.tick(view)
                            lastTickValue = newValue
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val h = size.height
                val w = size.width
                val fillH = h * animatedValue

                drawRoundRect(
                    color.copy(alpha = 0.2f),
                    Offset(0f, h - fillH),
                    Size(w, fillH),
                    CornerRadius(2f)
                )

                drawLine(
                    color.copy(alpha = 0.7f),
                    Offset(2.dp.toPx(), h - fillH),
                    Offset(w - 2.dp.toPx(), h - fillH),
                    1.5.dp.toPx()
                )

                drawLine(
                    Color.White.copy(alpha = 0.15f),
                    Offset(2.dp.toPx(), h - fillH - 1),
                    Offset(w - 2.dp.toPx(), h - fillH - 1),
                    0.5.dp.toPx()
                )

                for (i in 0..10) {
                    val tickY = h * i / 10
                    val tickW = if (i % 5 == 0) 6.dp.toPx() else 3.dp.toPx()
                    drawLine(
                        Color.White.copy(alpha = if (i % 5 == 0) 0.08f else 0.03f),
                        Offset(0f, tickY),
                        Offset(tickW, tickY),
                        0.5.dp.toPx()
                    )
                }
            }
        }
    }
}

@Composable
fun OscilloscopeDisplay(
    waveformData: List<Float>,
    color: Color = SynthCyan,
    secondaryColor: Color = SynthMagenta,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val scanPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing)),
        label = "scan"
    )

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .shadow(2.dp, RoundedCornerShape(4.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF08081A), Color(0xFF040410), Color(0xFF020208))
                )
            )
            .border(0.5.dp, color.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
    ) {
        val w = size.width
        val h = size.height
        val cy = h / 2

        for (i in 0..8) {
            val y = h * i / 8
            drawLine(color.copy(alpha = 0.03f), Offset(0f, y), Offset(w, y), 0.5f)
        }
        for (i in 0..10) {
            val x = w * i / 10
            drawLine(color.copy(alpha = 0.02f), Offset(x, 0f), Offset(x, h), 0.5f)
        }

        drawLine(color.copy(alpha = 0.06f), Offset(0f, cy), Offset(w, cy), 0.5f)

        if (waveformData.isNotEmpty()) {
            val path = Path()
            val segments = minOf(waveformData.size, 256)
            val step = w / segments

            for (i in 0 until segments) {
                val idx = (i.toFloat() / segments * waveformData.size).toInt().coerceIn(0, waveformData.size - 1)
                val x = i * step
                val y = cy - waveformData[idx] * cy * 0.8f
                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(path, secondaryColor.copy(alpha = 0.04f), style = Stroke(10f, cap = StrokeCap.Round))
            drawPath(path, color.copy(alpha = 0.1f), style = Stroke(4f, cap = StrokeCap.Round))
            drawPath(path, color.copy(alpha = 0.3f), style = Stroke(2f, cap = StrokeCap.Round))
            drawPath(path, color.copy(alpha = 0.7f), style = Stroke(1f, cap = StrokeCap.Round))
            drawPath(path, Color.White.copy(alpha = 0.2f), style = Stroke(0.5f, cap = StrokeCap.Round))
        }

        val scanX = scanPhase * w
        drawLine(color.copy(alpha = 0.04f), Offset(scanX, 0f), Offset(scanX, h), 1f)
    }
}

@Composable
fun SpectrumAnalyzer(
    spectrumData: FloatArray,
    color: Color = SynthCyan,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(3.dp))
            .shadow(2.dp, RoundedCornerShape(3.dp))
            .background(Color(0xFF0A0A1A))
    ) {
        if (spectrumData.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val barCount = 64
        val barWidth = w / barCount
        val binsPerBar = spectrumData.size / barCount

        for (i in 0 until barCount) {
            var sum = 0f
            for (b in 0 until binsPerBar) {
                val idx = (i * binsPerBar + b).coerceIn(0, spectrumData.size - 1)
                sum += spectrumData[idx]
            }
            val value = (sum / binsPerBar).coerceIn(0f, 1f)
            val barH = h * value
            val x = i * barWidth

            val hue = (i.toFloat() / barCount * 180f + 200f) % 360f
            val barColor = Color.hsl(hue, 0.7f, 0.45f)

            drawRoundRect(
                barColor.copy(alpha = 0.08f),
                Offset(x + 1, h - barH),
                Size(barWidth - 2, barH),
                CornerRadius(1f)
            )

            if (barH > 4f) {
                drawRoundRect(
                    barColor.copy(alpha = 0.25f),
                    Offset(x + 1, h - barH * 0.6f),
                    Size(barWidth - 2, barH * 0.6f),
                    CornerRadius(1f)
                )
            }

            drawRect(
                barColor.copy(alpha = 0.6f),
                Offset(x + 1, h - 1.5f),
                Size(barWidth - 2, 1.5f)
            )
        }
    }
}

@Composable
fun LEDIndicator(
    label: String,
    isActive: Boolean,
    activeColor: Color = SynthGreen,
    modifier: Modifier = Modifier
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(if (isBright) 7.dp else 5.dp)
                .clip(CircleShape)
                .background(if (isActive) activeColor.copy(alpha = (0.6f * boost).coerceIn(0f, 1f)) else activeColor.copy(alpha = (0.08f * boost).coerceIn(0f, 0.3f)))
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            label,
            color = if (isActive) activeColor.copy(alpha = (0.4f + 0.4f * boost).coerceIn(0f, 1f)) else DarkOnSurfaceVariant.copy(alpha = (0.15f + 0.15f * boost).coerceIn(0f, 0.5f)),
            fontSize = if (isBright) 9.sp else 7.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (isBright) FontWeight.Normal else FontWeight.Light
        )
    }
}

@Composable
fun PatchCable(
    from: String,
    to: String,
    color: Color = SynthCyan,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Text(from, color = color.copy(alpha = 0.3f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(color.copy(alpha = 0.1f))
        )
        Text(to, color = color.copy(alpha = 0.3f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun SynthButton(
    label: String,
    sublabel: String,
    isActive: Boolean,
    color: Color = SynthCyan,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val view = LocalView.current
    val glowIntensity by animateFloatAsState(
        targetValue = if (isActive) 0.25f else 0f,
        animationSpec = tween(200), label = "btnGlow"
    )

    SynthPanel(
        modifier = modifier
            .height(60.dp)
            .clickable {
                HapticEngine.click(view)
                onClick()
            },
        glowColor = color,
        glowIntensity = glowIntensity
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LEDIndicator(label, isActive, color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                sublabel,
                color = DarkOnSurfaceVariant.copy(alpha = 0.25f),
                fontSize = 6.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            )
        }
    }
}

@Composable
fun DynamicBackground(
    amplitude: Float = 0f,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val phase1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing)),
        label = "bg1"
    )
    val phase2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "bg2"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        drawRect(Color(0xFF05050C))

        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    SynthCyan.copy(alpha = 0.015f + amplitude * 0.02f),
                    Color.Transparent
                ),
                center = Offset(w * (0.3f + sin(phase1) * 0.2f), h * (0.25f + cos(phase1) * 0.15f)),
                radius = w * 0.6f
            )
        )
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    SynthMagenta.copy(alpha = 0.012f),
                    Color.Transparent
                ),
                center = Offset(w * (0.7f + sin(phase2) * 0.15f), h * (0.7f + cos(phase2) * 0.15f)),
                radius = w * 0.5f
            )
        )
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    SynthPurple.copy(alpha = 0.008f),
                    Color.Transparent
                ),
                center = Offset(w * 0.5f, h * (0.5f + sin(phase1 + phase2) * 0.1f)),
                radius = w * 0.4f
            )
        )
    }
}
