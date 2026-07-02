package com.example.myapplication.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.engine.DetectedHandpanResult
import com.example.myapplication.engine.HarmonicInfo
import com.example.myapplication.ui.theme.*

@Composable
fun HandpanInfoPanel(
    handpan: DetectedHandpanResult,
    modifier: Modifier = Modifier
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    val infiniteTransition = rememberInfiniteTransition()
    val glowPhase by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )

    SynthPanel(
        modifier = modifier.fillMaxWidth(),
        label = "HANDPAN_DETECTION",
        glowColor = SynthAmber,
        glowIntensity = handpan.confidence * 0.3f
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(if (isBright) 12.dp else 10.dp)
                            .clip(CircleShape)
                            .background(SynthAmber.copy(alpha = (0.6f + glowPhase * 0.3f).coerceIn(0f, 1f)))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "${handpan.brand} ${handpan.model}",
                            color = Color.White.copy(alpha = (0.8f + 0.2f * boost).coerceIn(0f, 1f)),
                            fontSize = if (isBright) 16.sp else 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            handpan.instrumentName,
                            color = SynthAmber.copy(alpha = 0.7f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${(handpan.confidence * 100).toInt()}%",
                        color = SynthAmber,
                        fontSize = if (isBright) 22.sp else 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text("CONF", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            HandpanNoteDisplay(handpan)

            Spacer(modifier = Modifier.height(12.dp))

            HandpanHarmonicsChart(handpan)

            Spacer(modifier = Modifier.height(12.dp))

            HandpanStructInfo(handpan)

            Spacer(modifier = Modifier.height(10.dp))

            HandpanSpectralInfo(handpan)
        }
    }
}

@Composable
private fun HandpanNoteDisplay(handpan: DetectedHandpanResult) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        alpha = 0.4f,
        cornerRadius = 10.dp,
        glowColor = SynthCyan,
        glowIntensity = 0.15f
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("NOTE", color = SynthCyan.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text(
                    handpan.detectedNote,
                    color = SynthCyan,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("OCTAVE", color = SynthMagenta.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text(
                    "${handpan.detectedOctave}",
                    color = SynthMagenta,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("CENTS", color = SynthGreen.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                val centsColor = when {
                    abs(handpan.centsOffset) <= 5 -> SynthGreen
                    abs(handpan.centsOffset) <= 15 -> SynthAmber
                    else -> SynthRed
                }
                Text(
                    "${if (handpan.centsOffset >= 0) "+" else ""}${handpan.centsOffset}",
                    color = centsColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Hz", color = SynthPurple.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text(
                    "${handpan.fundamentalHz.toInt()}",
                    color = SynthPurple,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
private fun HandpanHarmonicsChart(handpan: DetectedHandpanResult) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HARMONIC_PROFILE",
                color = SynthCyan.copy(alpha = (0.6f + 0.3f * boost).coerceIn(0f, 1f)),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            GlassPanel(alpha = 0.3f, cornerRadius = 6.dp) {
                Text(
                    "${handpan.harmonicCount} PARTIALS",
                    color = SynthGreen.copy(alpha = 0.8f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                .border(0.5.dp, SynthCyan.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
        ) {
            val cw = this.size.width
            val ch = this.size.height
            val maxAmp = handpan.harmonics.maxOfOrNull { it.amplitudeRatio } ?: 1f
            val barCount = minOf(handpan.harmonics.size, 12)
            val barWidth = cw / (barCount + 1)

            for (i in 0 until barCount) {
                val h = handpan.harmonics[i]
                val barH = (h.amplitudeRatio / maxAmp) * ch * 0.85f
                val x = (i + 0.5f) * barWidth
                val barColor = when (h.type) {
                    "Fundamental" -> SynthCyan
                    "Octave" -> SynthGreen
                    "Octave + 5th" -> SynthMagenta
                    "Two Octaves" -> SynthPurple
                    else -> SynthAmber
                }

                drawRoundRect(
                    barColor.copy(alpha = 0.1f),
                    Offset(x - barWidth * 0.35f, ch - barH - 4),
                    Size(barWidth * 0.7f, barH),
                    androidx.compose.ui.geometry.CornerRadius(3f)
                )
                drawRoundRect(
                    barColor.copy(alpha = 0.4f),
                    Offset(x - barWidth * 0.35f, ch - barH * 0.4f - 4),
                    Size(barWidth * 0.7f, barH * 0.4f),
                    androidx.compose.ui.geometry.CornerRadius(3f)
                )
                drawRoundRect(
                    barColor.copy(alpha = (0.6f + 0.3f * boost).coerceIn(0f, 1f)),
                    Offset(x - barWidth * 0.35f, ch - 3),
                    Size(barWidth * 0.7f, 2f),
                    androidx.compose.ui.geometry.CornerRadius(1f)
                )

                drawLine(
                    barColor.copy(alpha = 0.3f),
                    Offset(x, ch - barH - 4),
                    Offset(x, ch - 3),
                    1.dp.toPx()
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(minOf(handpan.harmonics.size, 8)) { idx ->
                val h = handpan.harmonics[idx]
                val barColor = when (h.type) {
                    "Fundamental" -> SynthCyan
                    "Octave" -> SynthGreen
                    "Octave + 5th" -> SynthMagenta
                    "Two Octaves" -> SynthPurple
                    else -> SynthAmber
                }
                GlassPanel(alpha = 0.25f, cornerRadius = 4.dp) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(barColor)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            h.type,
                            color = barColor.copy(alpha = 0.8f),
                            fontSize = 6.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            " ${h.frequency.toInt()}Hz",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 6.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HandpanStructInfo(handpan: DetectedHandpanResult) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    Column {
        Text(
            "INSTRUMENT_DATA",
            color = SynthAmber.copy(alpha = (0.6f + 0.3f * boost).coerceIn(0f, 1f)),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StructTag("BRAND", handpan.brand, SynthCyan, Modifier.weight(1f))
            StructTag("MODEL", handpan.model, SynthMagenta, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StructTag("MATERIAL", handpan.material, SynthAmber, Modifier.weight(1f))
            StructTag("SCALE", handpan.scale, SynthGreen, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StructTag("TUNING", handpan.tuningSystem, SynthPurple, Modifier.weight(1f))
            StructTag("SIZE", handpan.sizeCategory, SynthOrange, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StructTag("ATTACK", handpan.attackType, SynthCyan, Modifier.weight(1f))
            StructTag("SUSTAIN", handpan.sustainRating, SynthMagenta, Modifier.weight(1f))
        }
    }
}

@Composable
private fun HandpanSpectralInfo(handpan: DetectedHandpanResult) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    Column {
        Text(
            "SPECTRAL_ANALYSIS",
            color = SynthCyan.copy(alpha = (0.6f + 0.3f * boost).coerceIn(0f, 1f)),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        SpectralBar("OVERTONE_RATIO", handpan.overtoneRatio, SynthCyan)
        Spacer(modifier = Modifier.height(3.dp))
        SpectralBar("INHARMONICITY", handpan.inharmonicity, SynthMagenta)
        Spacer(modifier = Modifier.height(3.dp))
        SpectralBar("BRIGHTNESS", handpan.brightnessIndex, SynthAmber)
        Spacer(modifier = Modifier.height(3.dp))
        SpectralBar("WARMTH", handpan.warmthIndex, SynthGreen)
        Spacer(modifier = Modifier.height(6.dp))

        GlassPanel(alpha = 0.25f, cornerRadius = 6.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PROFILE", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                Text(handpan.spectralProfile, color = SynthCyan.copy(alpha = 0.8f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun SpectralBar(label: String, value: Float, color: Color) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = DarkOnSurfaceVariant.copy(alpha = (0.4f + 0.3f * boost).coerceIn(0f, 0.8f)),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(100.dp)
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(DarkSurfaceVariant.copy(alpha = 0.3f))
        ) {
            val fillW = size.width * value.coerceIn(0f, 1f)
            drawRoundRect(color.copy(alpha = 0.15f), Offset.Zero, Size(size.width, size.height), androidx.compose.ui.geometry.CornerRadius(3f))
            drawRoundRect(color.copy(alpha = 0.5f), Offset.Zero, Size(fillW, size.height), androidx.compose.ui.geometry.CornerRadius(3f))
            if (fillW > 3f) drawRoundRect(color.copy(alpha = 0.8f), Offset.Zero, Size(2f, size.height), androidx.compose.ui.geometry.CornerRadius(1f))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "${(value * 100).toInt()}%",
            color = color.copy(alpha = 0.8f),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
private fun StructTag(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassPanel(modifier = modifier, alpha = 0.25f, cornerRadius = 6.dp) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$label: ",
                color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                value,
                color = color.copy(alpha = 0.9f),
                fontSize = 8.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

private fun abs(value: Int): Int = if (value < 0) -value else value
