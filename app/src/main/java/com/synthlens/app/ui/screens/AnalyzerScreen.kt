package com.synthlens.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.engine.StemSynthProfile
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun AnalyzerScreen(
    audioEngine: AudioEngine,
    onNavigateToDetails: () -> Unit
) {
    val context = LocalContext.current
    val isRecording by audioEngine.isRecording.collectAsState()
    val analysis by audioEngine.analysis.collectAsState()
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) audioEngine.startRecording()
    }

    val detected = analysis.detectedSynth
    val detectedHandpan = analysis.detectedHandpan
    val stemColors = listOf(SynthPurple, SynthMagenta, SynthCyan, SynthGreen)

    val infiniteTransition = rememberInfiniteTransition()
    val bgPhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing)),
        label = "bgPhase"
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            drawRect(
                Brush.radialGradient(
                    colors = listOf(
                        SynthCyan.copy(alpha = 0.02f + analysis.amplitude * 0.03f),
                        SynthMagenta.copy(alpha = 0.015f),
                        Color(0xFF050010),
                        Color(0xFF020005)
                    ),
                    center = Offset(w * (0.3f + sin(bgPhase * PI.toFloat() * 2) * 0.2f), h * 0.3f),
                    radius = w * 0.8f
                )
            )
            drawRect(
                Brush.radialGradient(
                    colors = listOf(
                        SynthMagenta.copy(alpha = 0.015f),
                        Color.Transparent
                    ),
                    center = Offset(w * (0.7f + cos(bgPhase * PI.toFloat() * 2) * 0.2f), h * 0.7f),
                    radius = w * 0.6f
                )
            )
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LEDIndicator("PWR", true, SynthGreen)
                Spacer(modifier = Modifier.width(8.dp))
                LEDIndicator("REC", isRecording, SynthRed)
                Spacer(modifier = Modifier.width(8.dp))
                LEDIndicator("SIG", analysis.amplitude > 0.01f, SynthCyan)
            }
            Text(
                "SYNTHLENS",
                color = SynthCyan.copy(alpha = (0.7f + 0.3f * boost).coerceIn(0f, 1f)),
                fontSize = if (isBright) 14.sp else 12.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        SynthPanel(
            modifier = Modifier.fillMaxWidth(),
            label = "OSCILLATOR SCOPE",
            glowColor = SynthCyan,
            glowIntensity = if (isRecording) analysis.amplitude * 0.3f else 0f
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LEDIndicator("WAVE", analysis.waveformType != "Unknown", SynthCyan)
                    LEDIndicator("FREQ", analysis.frequency > 20f, SynthAmber)
                    LEDIndicator("HARM", analysis.harmonics.isNotEmpty(), SynthMagenta)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OscilloscopeDisplay(
                    waveformData = analysis.waveformPoints,
                    color = SynthCyan,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SynthKnob(
                        value = if (analysis.amplitude.isFinite()) analysis.amplitude.coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "LEVEL",
                        color = SynthCyan,
                        knobSize = 52.dp
                    )
                    SynthKnob(
                        value = if (analysis.frequency.isFinite()) (analysis.frequency / 2000f).coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "FREQ",
                        color = SynthAmber,
                        knobSize = 52.dp
                    )
                    SynthKnob(
                        value = if (analysis.thd.isFinite()) analysis.thd.coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "THD",
                        color = SynthMagenta,
                        knobSize = 52.dp
                    )
                    SynthKnob(
                        value = if (analysis.spectralFlatness.isFinite()) analysis.spectralFlatness.coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "FLAT",
                        color = SynthGreen,
                        knobSize = 52.dp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        SynthPanel(
            modifier = Modifier.fillMaxWidth(),
            label = "FILTER BANK",
            glowColor = SynthMagenta,
            glowIntensity = if (isRecording) 0.1f else 0f
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    SynthSlider(
                        value = if (analysis.rmsLevel.isFinite()) (analysis.rmsLevel / -60f).coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "LPF",
                        color = SynthMagenta,
                        modifier = Modifier.weight(1f)
                    )
                    SynthSlider(
                        value = if (analysis.spectralRolloff.isFinite()) (analysis.spectralRolloff / 20000f).coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "HPF",
                        color = SynthCyan,
                        modifier = Modifier.weight(1f)
                    )
                    SynthSlider(
                        value = if (analysis.spectralBandwidth.isFinite()) (analysis.spectralBandwidth / 10000f).coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "RES",
                        color = SynthPurple,
                        modifier = Modifier.weight(1f)
                    )
                    SynthSlider(
                        value = if (analysis.harmonicToNoiseRatio.isFinite()) analysis.harmonicToNoiseRatio.coerceIn(0f, 1f) else 0f,
                        onValueChange = {},
                        label = "DRIVE",
                        color = SynthAmber,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PatchCable("IN", "LPF", SynthMagenta)
                    PatchCable("LPF", "HPF", SynthCyan)
                    PatchCable("HPF", "OUT", SynthGreen)
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (isRecording && analysis.stemProfiles.isNotEmpty()) {
            SynthPanel(
                modifier = Modifier.fillMaxWidth(),
                label = "STEM分离 ENGINE",
                glowColor = SynthMagenta,
                glowIntensity = 0.15f
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    analysis.stemProfiles.forEachIndexed { index, profile ->
                        val color = stemColors.getOrElse(index) { SynthAmber }
                        SynthStemRow(profile, color, analysis.amplitude)
                        if (index < analysis.stemProfiles.lastIndex) {
                            Spacer(modifier = Modifier.height(4.dp))
                            PatchCable(profile.stemName, profile.detectedSynth.take(15), color)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    analysis.dominantStemName?.let { dominant ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LEDIndicator("DOM", true, SynthAmber)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                dominant,
                                color = SynthAmber.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (detected != null) {
            SynthPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToDetails() },
                label = "DETECTION OUTPUT",
                glowColor = SynthGreen,
                glowIntensity = detected.confidence * 0.3f
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LEDIndicator("LOCK", true, SynthGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "${detected.brand} ${detected.name}",
                                    color = Color.White.copy(alpha = (0.7f + 0.3f * boost).coerceIn(0f, 1f)),
                                    fontSize = if (isBright) 16.sp else 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    detected.category,
                                    color = DarkOnSurfaceVariant.copy(alpha = (0.5f + 0.3f * boost).coerceIn(0f, 1f)),
                                    fontSize = if (isBright) 10.sp else 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "${(detected.confidence * 100).toInt()}%",
                                color = SynthGreen,
                                fontSize = if (isBright) 22.sp else 18.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = FontFamily.Monospace
                            )
                            Text("CONF", color = DarkOnSurfaceVariant.copy(alpha = (0.4f + 0.4f * boost).coerceIn(0f, 1f)), fontSize = if (isBright) 9.sp else 7.sp, fontFamily = FontFamily.Monospace)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    SpectrumAnalyzer(
                        spectrumData = analysis.spectrumData,
                        color = SynthGreen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(
                            "WAVE" to detected.waveformType,
                            "FILT" to detected.filterType.take(15),
                            "OSC" to detected.oscillators.take(15)
                        ).forEach { (label, value) ->
                            SynthChip(label, value, SynthCyan, Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "TAP FOR FULL ANALYSIS",
                            color = SynthCyan.copy(alpha = (0.5f + 0.4f * boost).coerceIn(0f, 1f)),
                            fontSize = if (isBright) 10.sp else 8.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = SynthCyan.copy(alpha = (0.5f + 0.4f * boost).coerceIn(0f, 1f)), modifier = Modifier.size(if (isBright) 14.dp else 12.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (detectedHandpan != null) {
            HandpanInfoPanel(handpan = detectedHandpan)
        }

        Spacer(modifier = Modifier.height(6.dp))

        SynthPanel(
            modifier = Modifier.fillMaxWidth(),
            label = "CONTROLS"
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SynthButton(
                    label = if (isRecording) "STOP" else "START",
                    sublabel = "MIC INPUT",
                    isActive = isRecording,
                    color = if (isRecording) SynthRed else SynthGreen,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        } else if (isRecording) {
                            audioEngine.stopRecording()
                        } else {
                            audioEngine.startRecording()
                        }
                    }
                )
                SynthButton(
                    label = "SYSTEM",
                    sublabel = "AUDIO INT.",
                    isActive = false,
                    color = SynthAmber,
                    modifier = Modifier.weight(1f),
                    onClick = { }
                )
            }
        }

        if (!isRecording) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                "PRESS START TO BEGIN DETECTION",
                color = DarkOnSurfaceVariant.copy(alpha = (0.3f + 0.4f * boost).coerceIn(0f, 0.9f)),
                fontSize = if (isBright) 12.sp else 10.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
    }
}

@Composable
private fun SynthStemRow(
    profile: StemSynthProfile,
    color: Color,
    amplitude: Float
) {
    val safeEnergy = if (profile.energy.isFinite()) profile.energy else 0f
    val energy by animateFloatAsState(
        targetValue = (safeEnergy * 4f).coerceIn(0f, 1f),
        animationSpec = tween(150), label = "energy"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.3f + energy * 0.7f))
                .border(0.5.dp, color.copy(alpha = 0.2f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            profile.stemName,
            color = color.copy(alpha = 0.7f),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.width(50.dp)
        )
        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF0A0A12))
        ) {
            val fillW = size.width * energy
            drawRoundRect(color.copy(alpha = 0.08f), Offset.Zero, Size(size.width, size.height), CornerRadius(2f))
            drawRoundRect(color.copy(alpha = 0.5f), Offset.Zero, Size(fillW, size.height), CornerRadius(2f))
            if (fillW > 2f) drawRoundRect(color.copy(alpha = 0.8f), Offset.Zero, Size(2f, size.height), CornerRadius(1f))
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            "${(profile.confidence * 100).toInt()}%",
            color = if (profile.confidence > 0.5f) SynthGreen else SynthAmber,
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun SynthButton(
    label: String,
    sublabel: String,
    isActive: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    val view = LocalView.current
    val glowIntensity by animateFloatAsState(
        targetValue = if (isActive) 0.3f else 0f,
        animationSpec = tween(200), label = "glow"
    )

    SynthPanel(
        modifier = modifier
            .height(64.dp)
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
                color = DarkOnSurfaceVariant.copy(alpha = (0.4f + 0.4f * boost).coerceIn(0f, 0.9f)),
                fontSize = if (isBright) 8.sp else 6.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun SynthChip(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    SynthPanel(modifier = modifier, alpha = (0.3f * boost).coerceIn(0f, 0.8f)) {
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
            Text(label, color = color.copy(alpha = (0.5f + 0.3f * boost).coerceIn(0f, 1f)), fontSize = if (isBright) 8.sp else 6.sp, fontFamily = FontFamily.Monospace)
            Text(value, color = Color.White.copy(alpha = (0.8f * boost).coerceIn(0f, 1f)), fontSize = if (isBright) 10.sp else 8.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
        }
    }
}
