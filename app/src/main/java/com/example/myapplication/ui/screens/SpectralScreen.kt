package com.example.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.engine.AudioEngine
import com.example.myapplication.engine.StemSynthProfile
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*

enum class VisualizerMode(val label: String, val tag: String) {
    TERRAIN("TERRAIN_3D", "TERRAIN"),
    ORBITAL("RADIAL_ORBIT", "ORBITAL"),
    CLASSIC("CLASSIC", "CLASSIC")
}

@Composable
fun SpectralScreen(
    audioEngine: AudioEngine
) {
    val analysis by audioEngine.analysis.collectAsState()
    val isRecording by audioEngine.isRecording.collectAsState()
    val reactiveAlpha = rememberReactiveAmplitude(analysis.amplitude, 0.4f)
    val reactiveGlow = rememberReactiveGlow(analysis.amplitude)
    var visualizerMode by remember { mutableStateOf(VisualizerMode.TERRAIN) }

    val spectrumHistory = remember { mutableStateListOf<FloatArray>() }

    LaunchedEffect(analysis.spectrumData) {
        if (analysis.spectrumData.isNotEmpty()) {
            spectrumHistory.add(analysis.spectrumData.copyOf())
            if (spectrumHistory.size > 40) {
                spectrumHistory.removeAt(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ANALIZADOR_ESPECTRAL",
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
            Text(
                text = if (isRecording) "● REC" else "○ OFF",
                color = if (isRecording) SynthRed else DarkOnSurfaceVariant.copy(alpha = 0.4f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            VisualizerMode.entries.forEach { mode ->
                val isSelected = visualizerMode == mode
                GlassPanel(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    alpha = if (isSelected) 0.5f else 0.2f,
                    cornerRadius = 10.dp,
                    glowColor = if (isSelected) SynthCyan else Color.Transparent,
                    glowIntensity = if (isSelected) 0.3f else 0f
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                if (isSelected) SynthCyan.copy(alpha = 0.08f)
                                else Color.Transparent
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            color = if (isSelected) SynthCyan else DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.45f,
            reactiveAmplitude = analysis.amplitude
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = when (visualizerMode) {
                            VisualizerMode.TERRAIN -> "TERRAIN_WATERFALL"
                            VisualizerMode.ORBITAL -> "RADIAL_ORBITAL_SPHERE"
                            VisualizerMode.CLASSIC -> "CLASSIC_BARS"
                        },
                        color = when (visualizerMode) {
                            VisualizerMode.TERRAIN -> SynthGreen
                            VisualizerMode.ORBITAL -> SynthAmber
                            VisualizerMode.CLASSIC -> SynthCyan
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "MODE_${visualizerMode.tag}",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                when (visualizerMode) {
                    VisualizerMode.TERRAIN -> {
                        TerrainWaterfall(
                            spectrumHistory = spectrumHistory.toList(),
                            amplitude = analysis.amplitude,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                        )
                    }
                    VisualizerMode.ORBITAL -> {
                        RadialOrbitalSphere(
                            spectrumData = analysis.spectrumData,
                            amplitude = analysis.amplitude,
                            harmonicProfile = analysis.harmonics,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                        )
                    }
                    VisualizerMode.CLASSIC -> {
                        SpectrumBars(
                            spectrumData = analysis.spectrumData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("20Hz", "200Hz", "1kHz", "5kHz", "20kHz").forEach { label ->
                        Text(
                            label,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.35f),
                            fontSize = 7.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            reactiveAmplitude = analysis.amplitude
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "WAVEFORM",
                    color = SynthGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                WaveformDisplay(
                    waveformPoints = analysis.waveformPoints,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            reactiveAmplitude = analysis.amplitude
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "HARMONIC_WATERFALL",
                    color = SynthMagenta,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                SpectrogramWaterfall(
                    spectrumHistory = spectrumHistory.toList(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("20Hz", color = DarkOnSurfaceVariant.copy(alpha = 0.35f), fontSize = 7.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
                    Text("20kHz", color = DarkOnSurfaceVariant.copy(alpha = 0.35f), fontSize = 7.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            reactiveAmplitude = analysis.amplitude
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "LIVE_MEASUREMENTS",
                    color = SynthAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassMeasurementItem("FREQ_FUND", String.format("%.1f Hz", analysis.frequency))
                    GlassMeasurementItem("OCTAVE", "${analysis.octaves}")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassMeasurementItem("RMS_LVL", String.format("%.1f dB", analysis.rmsLevel))
                    GlassMeasurementItem("PEAK_LVL", String.format("%.1f dB", analysis.peakLevel))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassMeasurementItem("THD", String.format("%.2f%%", analysis.thd * 100))
                    GlassMeasurementItem("WAVEFORM", analysis.waveformType)
                }
            }
        }

        if (analysis.stemProfiles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.4f,
                reactiveAmplitude = analysis.amplitude
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STEM_FREQ_BANDS",
                            color = SynthMagenta,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        analysis.dominantStemName?.let { dominant ->
                            GlassChip {
                                Text(
                                    text = "DOM: $dominant",
                                    color = SynthAmber,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    StemBandOverlay(
                        stemProfiles = analysis.stemProfiles,
                        amplitude = analysis.amplitude,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val stemColors = listOf(SynthPurple, SynthMagenta, SynthCyan, SynthGreen)
                    analysis.stemProfiles.forEachIndexed { index, profile ->
                        val color = stemColors.getOrElse(index) { SynthAmber }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = profile.stemName,
                                color = color,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.width(60.dp)
                            )
                            Text(
                                text = profile.frequencyRange,
                                color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(70.dp)
                            )
                            Text(
                                text = profile.waveformType,
                                color = DarkOnSurface.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(50.dp)
                            )
                            Text(
                                text = "${profile.peakFrequency.toInt()}Hz",
                                color = DarkOnSurface.copy(alpha = 0.6f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(55.dp)
                            )
                            GlassChip(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "${(profile.confidence * 100).toInt()}% ${profile.detectedSynth.take(12)}",
                                    color = if (profile.confidence > 0.5f) SynthGreen else DarkOnSurfaceVariant,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    maxLines = 1
                                )
                            }
                        }
                        if (index < analysis.stemProfiles.lastIndex) {
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun GlassMeasurementItem(label: String, value: String) {
    GlassChip {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun StemBandOverlay(
    stemProfiles: List<StemSynthProfile>,
    amplitude: Float,
    modifier: Modifier = Modifier
) {
    val stemColors = listOf(SynthPurple, SynthMagenta, SynthCyan, SynthGreen)
    val totalEnergy = stemProfiles.sumOf { it.energy.toDouble() }.toFloat().coerceAtLeast(0.001f)

    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
    ) {
        val w = size.width
        val h = size.height

        for (i in 0..8) {
            val y = h * i / 8
            drawLine(DarkOnSurfaceVariant.copy(alpha = 0.06f), Offset(0f, y), Offset(w, y), 0.5f)
        }

        val bandWidth = w / stemProfiles.size

        stemProfiles.forEachIndexed { index, profile ->
            val color = stemColors.getOrElse(index) { SynthAmber }
            val weight = (profile.energy / totalEnergy).coerceIn(0.05f, 1f)
            val bandX = index * bandWidth
            val barHeight = h * weight * (0.4f + amplitude * 0.6f)

            val barGradient = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = 0.02f),
                    color.copy(alpha = 0.15f + amplitude * 0.15f),
                    color.copy(alpha = 0.3f + amplitude * 0.3f)
                ),
                startY = h - barHeight,
                endY = h
            )

            drawRect(
                brush = barGradient,
                topLeft = Offset(bandX + 2f, h - barHeight),
                size = Size(bandWidth - 4f, barHeight)
            )

            drawLine(
                color.copy(alpha = 0.6f + amplitude * 0.3f),
                Offset(bandX + bandWidth / 2, h - barHeight),
                Offset(bandX + bandWidth / 2, h),
                2f
            )

            drawCircle(
                color.copy(alpha = 0.4f),
                3f,
                Offset(bandX + bandWidth / 2, h - barHeight)
            )
            drawCircle(
                color.copy(alpha = 0.8f),
                1.5f,
                Offset(bandX + bandWidth / 2, h - barHeight)
            )
        }
    }
}
