package com.synthlens.app.ui.screens

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
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.viewmodel.createSynthViewModel
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.engine.StemSynthProfile
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel
import kotlin.math.*

@Composable
fun AnalysisDetailsScreen(
    audioEngine: AudioEngine,
    onBack: () -> Unit,
    viewModel: SynthViewModel = createSynthViewModel()
) {
    val analysis by audioEngine.analysis.collectAsState()
    val detected = analysis.detectedSynth
    var saved by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = SynthCyan, modifier = Modifier.size(20.dp))
                }
                Text(
                    "OSC_SCANNER_V1",
                    color = DarkOnSurface,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
            Text(
                "ANALYSIS_V2",
                color = SynthAmber,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (detected != null) {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.45f,
                cornerRadius = 12.dp,
                glowColor = SynthCyan,
                glowIntensity = 0.2f
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "IDENTIFIED // ${detected.category.uppercase().replace(" ", "_")}",
                                color = SynthCyan.copy(alpha = 0.6f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.sp
                            )
                            Text(
                                "${detected.brand} ${detected.name}".uppercase(),
                                color = DarkOnSurface,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 2.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                String.format("%.1f%%", detected.confidence * 100),
                                color = SynthGreen,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Monospace
                            )
                            Text("CONFIDENCE", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("TECHNICAL_MANIFEST", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestCol("WAVEFORM", detected.waveformType.uppercase(), Modifier.weight(1f))
                        ManifestCol("FREQ", String.format("%.1f Hz", analysis.frequency), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestCol("FILTER", detected.filterType.uppercase().take(25), Modifier.weight(1f))
                        ManifestCol("OCTAVE", "${analysis.octaves}", Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ManifestCol("OSCILLATORS", detected.oscillators.uppercase().take(35), Modifier.fillMaxWidth())
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("MODULATION", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    ModRoute("LFO", "→ FREQ: ${String.format("%.1f Hz", analysis.frequency * 0.02f)}", SynthCyan)
                    Spacer(modifier = Modifier.height(6.dp))
                    ModRoute("ENV", "→ ${detected.modulation}", SynthGreen)
                    Spacer(modifier = Modifier.height(6.dp))
                    ModRoute("MOD", "→ DEPTH: ESTIMATED", SynthAmber)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SIGNAL_ANALYSIS", color = SynthMagenta, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    MiniSpectrumCanvas(
                        spectrumData = analysis.spectrumData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestCol("RMS", String.format("%.1f dB", analysis.rmsLevel), Modifier.weight(1f))
                        ManifestCol("PEAK", String.format("%.1f dB", analysis.peakLevel), Modifier.weight(1f))
                        ManifestCol("THD", String.format("%.2f%%", analysis.thd * 100), Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (analysis.stemProfiles.isNotEmpty()) {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    alpha = 0.35f,
                    cornerRadius = 12.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("STEM_BREAKDOWN", color = SynthMagenta, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                            analysis.stemAnalysis?.let { stemAnalysis ->
                                Text(
                                    "CONF: ${(stemAnalysis.separationConfidence * 100).toInt()}%",
                                    color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        analysis.dominantStemName?.let { dominant ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(SynthAmber))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "DOMINANT: $dominant",
                                    color = SynthAmber,
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        val stemColors = listOf(SynthPurple, SynthMagenta, SynthCyan, SynthGreen)
                        analysis.stemProfiles.forEachIndexed { index, profile ->
                            DetailStemCard(
                                profile = profile,
                                color = stemColors.getOrElse(index) { SynthAmber },
                                isDominant = profile.stemName == analysis.dominantStemName
                            )
                            if (index < analysis.stemProfiles.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (detected.effects.isNotEmpty() && detected.effects != "None") {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    alpha = 0.35f,
                    cornerRadius = 12.dp
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("EFFECTS_CHAIN", color = SynthPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(detected.effects.uppercase(), color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.addDetectedSynth(
                            com.synthlens.app.data.DetectedSynth(
                                name = "${detected.brand} ${detected.name}",
                                brand = detected.brand,
                                category = detected.category,
                                confidence = detected.confidence,
                                frequencySignature = detected.frequencySignature,
                                waveformType = detected.waveformType,
                                filterType = detected.filterType
                            )
                        )
                        saved = true
                    },
                alpha = if (saved) 0.3f else 0.45f,
                cornerRadius = 12.dp,
                glowColor = if (saved) SynthGreen else SynthCyan,
                glowIntensity = if (saved) 0.1f else 0.2f
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (saved) Icons.Default.Check else Icons.Default.Bookmark,
                        null,
                        tint = if (saved) SynthGreen else SynthCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        if (saved) "SAVED_TO_SYSTEM_LIBRARY" else "SAVE_TO_SYSTEM_LIBRARY",
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = if (saved) SynthGreen else SynthCyan
                    )
                }
            }
        } else {
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                alpha = 0.3f
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Radar, null, tint = DarkOnSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(14.dp))
                        Text("SCANNING_FOR_SIGNALS...", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 12.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                        Text("Awaiting audio input", color = DarkOnSurfaceVariant.copy(alpha = 0.3f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ManifestCol(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 8.dp)) {
        Text("$label:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
        Text(value, color = DarkOnSurface, fontSize = 10.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun ModRoute(label: String, route: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = color, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(40.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = color.copy(alpha = 0.5f), modifier = Modifier.size(10.dp))
        Text(route, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun MiniSpectrumCanvas(spectrumData: FloatArray, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
    ) {
        if (spectrumData.isEmpty()) return@Canvas
        val w = size.width
        val h = size.height
        val path = Path()
        val segments = 64
        val binsPerSegment = spectrumData.size / segments

        for (i in 0..segments) {
            val x = (i.toFloat() / segments) * w
            val binStart = (i.toFloat() / segments * spectrumData.size).toInt().coerceIn(0, spectrumData.size - 1)
            val binEnd = ((i + 1).toFloat() / segments * spectrumData.size).toInt().coerceIn(binStart, spectrumData.size)
            var sum = 0f
            for (b in binStart until binEnd) {
                if (b < spectrumData.size) sum += spectrumData[b]
            }
            val value = if (binsPerSegment > 0) sum / binsPerSegment else 0f
            val y = h - (value * h * 3f).coerceIn(0f, h * 0.95f)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path, SynthCyan.copy(alpha = 0.15f), style = Stroke(width = 4f))
        drawPath(path, SynthCyan.copy(alpha = 0.4f), style = Stroke(width = 2f))
        drawPath(path, SynthCyan.copy(alpha = 0.8f), style = Stroke(width = 1f))
    }
}

@Composable
private fun DetailStemCard(
    profile: StemSynthProfile,
    color: Color,
    isDominant: Boolean
) {
    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        alpha = if (isDominant) 0.4f else 0.25f,
        cornerRadius = 8.dp,
        glowColor = color,
        glowIntensity = if (isDominant) 0.2f else 0f
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = profile.stemName,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = profile.frequencyRange,
                    color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailStemCol("SYNTH", profile.detectedSynth.take(16), Modifier.weight(1f))
                DetailStemCol("BRAND", profile.brand, Modifier.weight(1f))
                DetailStemCol("CONF", "${(profile.confidence * 100).toInt()}%", Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                DetailStemCol("WAVE", profile.waveformType, Modifier.weight(1f))
                DetailStemCol("FILTER", profile.filterType.take(20), Modifier.weight(1f))
                DetailStemCol("PEAK", "${profile.peakFrequency.toInt()}Hz", Modifier.weight(1f))
            }
            if (profile.characteristics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    profile.characteristics["oscillators"]?.let { osc ->
                        GlassChip(modifier = Modifier.weight(1f)) {
                            Text(
                                "OSC: ${osc.take(18)}",
                                color = DarkOnSurface.copy(alpha = 0.7f),
                                fontSize = 7.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                maxLines = 1
                            )
                        }
                    }
                    profile.characteristics["effects"]?.let { eff ->
                        if (eff != "None") {
                            GlassChip(modifier = Modifier.weight(1f)) {
                                Text(
                                    "FX: ${eff.take(18)}",
                                    color = DarkOnSurface.copy(alpha = 0.7f),
                                    fontSize = 7.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailStemCol(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(end = 4.dp)) {
        Text(
            "$label:",
            color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
            fontSize = 6.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
        Text(
            value,
            color = DarkOnSurface.copy(alpha = 0.8f),
            fontSize = 8.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}
