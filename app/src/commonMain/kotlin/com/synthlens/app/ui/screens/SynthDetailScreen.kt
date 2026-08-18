package com.synthlens.app.ui.screens

import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
// import com.synthlens.app.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size as GeoSize
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.data.SynthLibraryItem
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import kotlin.math.*

@Composable
fun SynthDetailScreen(
    synth: SynthLibraryItem,
    onBack: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val scanLine by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing)
        )
    )

    Box(modifier = Modifier.fillMaxSize().background(
        when {
            synth.category.contains("DRUM", ignoreCase = true) -> Color(0xFF1A1A24)
            synth.category.contains("MODULAR", ignoreCase = true) -> Color(0xFF241A1A)
            else -> Color(0xFF1A241A)
        }.copy(alpha = 0.5f)
    )) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                    "HARDWARE_SPEC",
                    color = SynthAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.4f,
            cornerRadius = 12.dp,
            glowColor = SynthCyan,
            glowIntensity = 0.15f
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            "IDENTIFIED // ${synth.category.uppercase().replace(" ", "_")}",
                            color = SynthCyan.copy(alpha = 0.6f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            synth.name.uppercase(),
                            color = DarkOnSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Text(
                            synth.brand.uppercase(),
                            color = SynthAmber.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            synth.category,
                            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        if (synth.yearReleased > 0) {
                            Text(
                                "${synth.yearReleased}",
                                color = SynthGreen,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
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
                Text(
                    "TECHNICAL_MANIFEST",
                    color = SynthAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ManifestItem("OSCILLATORS", synth.oscillators.uppercase().take(25), Modifier.weight(1f))
                    ManifestItem("FILTER", synth.filterTypes.uppercase().take(25), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    ManifestItem("POLYPHONY", synth.polyphony.uppercase(), Modifier.weight(1f))
                    ManifestItem("MODULATION", "LFO_0.1Hz-50Hz", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(10.dp))
                if (synth.waveformTypes.isNotEmpty()) {
                    ManifestItem("WAVEFORMS", synth.waveformTypes.uppercase().take(40), Modifier.fillMaxWidth())
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "OSCILLATOR_BANK",
                        color = SynthCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Text(
                        "ACTIVE",
                        color = SynthGreen.copy(alpha = 0.6f),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val waveforms = synth.waveformTypes.split(",").take(3)
                    waveforms.forEachIndexed { index, wave ->
                        val waveName = wave.trim().uppercase()
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WaveformMiniCanvas(
                                type = waveName,
                                amplitude = pulse,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                waveName.take(10),
                                color = SynthCyan.copy(alpha = 0.7f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    if (waveforms.isEmpty()) {
                        repeat(3) { index ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                WaveformMiniCanvas(
                                    type = listOf("SAW", "SQUARE", "SINE")[index],
                                    amplitude = pulse,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    listOf("SAW", "SQUARE", "SINE")[index],
                                    color = SynthCyan.copy(alpha = 0.7f),
                                    fontSize = 8.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
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
                Text(
                    "FILTER_CORE",
                    color = SynthMagenta,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "TYPE:",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            synth.filterTypes.uppercase().take(30),
                            color = SynthMagenta.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    KnobVisualization(
                        label = "CUTOFF",
                        value = "12.4 kHz",
                        color = SynthCyan,
                        modifier = Modifier.size(60.dp)
                    )
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
                Text(
                    "MOD_MATRIX",
                    color = SynthGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                ModRoute("LFO_1", "→ VCF_CUTOFF", SynthCyan)
                Spacer(modifier = Modifier.height(6.dp))
                ModRoute("ENV_2", "→ VCA_AMP", SynthGreen)
                Spacer(modifier = Modifier.height(6.dp))
                ModRoute("MOD_WHEEL", "→ OSC_PWM", SynthAmber)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.35f,
            cornerRadius = 12.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    "HISTORY_AND_LEGACY",
                    color = SynthAmber,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    synth.description,
                    color = DarkOnSurface.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Light
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassPanel(
                        modifier = Modifier.weight(1f),
                        alpha = 0.3f,
                        cornerRadius = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PRODUCTION", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                if (synth.yearDiscontinued > 0) "${synth.yearReleased} — ${synth.yearDiscontinued}" else "${synth.yearReleased} — PRESENT",
                                color = SynthAmber,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassPanel(
                        modifier = Modifier.weight(1f),
                        alpha = 0.3f,
                        cornerRadius = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("ORIGIN", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                            Text(
                                synth.countryOfOrigin.uppercase().take(20),
                                color = SynthCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        if (synth.famousUsers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        "NOTABLE_USERS",
                        color = SynthPurple,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        synth.famousUsers,
                        color = DarkOnSurface.copy(alpha = 0.7f),
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Light
                    )
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
                Text(
                    "TECHNICAL_SPECS",
                    color = SynthCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                SpecRow("SIGNAL_CHAIN", synth.signalChain)
                SpecRow("POWER", synth.powerType)
                SpecRow("DIMENSIONS", synth.dimensions)
                SpecRow("WEIGHT", synth.weight)
                SpecRow("CONNECTIVITY", synth.connectivity)
                SpecRow("PRESETS", synth.presets)
            }
        }

        if (synth.priceRange.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.45f,
                cornerRadius = 12.dp,
                glowColor = SynthGreen,
                glowIntensity = 0.1f
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("PRICE_RANGE", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Text(synth.priceRange, color = SynthGreen, fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace)
                        }
                        if (synth.purchaseUrl.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    uriHandler.openUri(synth.purchaseUrl)
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, null, modifier = Modifier.size(14.dp), tint = SynthGreen)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("BUY", color = SynthGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.4f,
                cornerRadius = 10.dp,
                glowColor = SynthCyan,
                glowIntensity = 0.2f
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (synth.officialSite.isNotEmpty()) {
                                uriHandler.openUri(synth.officialSite)
                            }
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Language, null, tint = SynthCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OFFICIAL_SITE", color = SynthCyan, fontSize = 9.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                }
            }
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.4f,
                cornerRadius = 10.dp,
                glowColor = SynthAmber,
                glowIntensity = 0.2f
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (synth.soundDemos.isNotEmpty()) {
                                uriHandler.openUri(synth.soundDemos)
                            }
                        }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = SynthAmber, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("DEMOS", color = SynthAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ManifestItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 8.dp)) {
        Text(
            "$label:",
            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 7.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )
        Text(
            value,
            color = DarkOnSurface,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Light
        )
    }
}

@Composable
private fun WaveformMiniCanvas(type: String, amplitude: Float, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurfaceVariant)
            .border(0.5.dp, DarkBorder, RoundedCornerShape(8.dp))
    ) {
        val w = size.width
        val h = size.height
        val cy = h / 2
        val path = Path()

        val segments = 80
        for (i in 0..segments) {
            val x = (i.toFloat() / segments) * w
            val normalizedX = i.toFloat() / segments
            val y = when (type) {
                "SAW", "SAWTOOTH" -> {
                    val t = (normalizedX * 2f) % 1f
                    cy - (1f - 2f * t) * (h * 0.35f) * amplitude
                }
                "SQUARE" -> {
                    val t = (normalizedX * 2f) % 1f
                    cy - (if (t < 0.5f) 1f else -1f) * (h * 0.35f) * amplitude
                }
                "TRIANGLE", "TRI" -> {
                    val t = (normalizedX * 2f) % 1f
                    cy - (1f - 4f * abs(t - 0.5f)) * (h * 0.35f) * amplitude
                }
                "PULSE" -> {
                    val t = (normalizedX * 2f) % 1f
                    cy - (if (t < 0.3f) 1f else -1f) * (h * 0.35f) * amplitude
                }
                else -> {
                    cy - sin(normalizedX * PI.toFloat() * 4) * (h * 0.35f) * amplitude
                }
            }
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path, SynthCyan.copy(alpha = 0.15f), style = Stroke(width = 4f))
        drawPath(path, SynthCyan.copy(alpha = 0.4f), style = Stroke(width = 2f))
        drawPath(path, SynthCyan.copy(alpha = 0.8f), style = Stroke(width = 1f))
    }
}

@Composable
private fun KnobVisualization(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(40.dp)) {
            val cx = size.width / 2
            val cy = size.height / 2
            val r = size.width * 0.38f

            drawCircle(DarkBorder, r, Offset(cx, cy), style = Stroke(width = 2f))
            drawCircle(DarkBorder.copy(alpha = 0.3f), r * 0.7f, Offset(cx, cy), style = Stroke(width = 0.5f))

            val angle = -135f + 270f * 0.7f
            val rad = angle * PI.toFloat() / 180f
            drawLine(
                color,
                Offset(cx, cy),
                Offset(cx + cos(rad) * r * 0.8f, cy + sin(rad) * r * 0.8f),
                strokeWidth = 2f
            )
            drawCircle(color, 3f, Offset(cx, cy))
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = color.copy(alpha = 0.8f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun ModRoute(from: String, to: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(from, color = color, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = color.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
        Text(to, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color.copy(alpha = 0.15f))
                .border(0.5.dp, color.copy(alpha = 0.3f), RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check, null, tint = color, modifier = Modifier.size(10.dp))
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.35f))
            Text(value, color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.65f))
        }
    }
}
