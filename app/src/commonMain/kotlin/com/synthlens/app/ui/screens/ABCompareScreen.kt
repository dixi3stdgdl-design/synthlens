package com.synthlens.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.viewmodel.createSynthViewModel
import com.synthlens.app.data.SynthLibraryItem
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel
import kotlin.math.*

@Composable
fun ABCompareScreen(
    audioEngine: com.synthlens.app.engine.AudioEngine? = null,
    viewModel: SynthViewModel = createSynthViewModel()
) {
    val allSynths by viewModel.allSynths.collectAsState()
    var synthA by remember { mutableStateOf<SynthLibraryItem?>(null) }
    var synthB by remember { mutableStateOf<SynthLibraryItem?>(null) }
    var showPickerA by remember { mutableStateOf(false) }
    var showPickerB by remember { mutableStateOf(false) }

    val analysis = audioEngine?.analysis?.collectAsState()

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
                "A/B_COMPARE",
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
            Text(
                "DUAL_ANALYSIS",
                color = SynthMagenta,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SynthSelectorCard(
                label = "SYNTH_A",
                color = SynthCyan,
                synth = synthA,
                onClick = { showPickerA = true },
                modifier = Modifier.weight(1f)
            )
            SynthSelectorCard(
                label = "SYNTH_B",
                color = SynthMagenta,
                synth = synthB,
                onClick = { showPickerB = true },
                modifier = Modifier.weight(1f)
            )
        }

        if (synthA != null && synthB != null) {
            Spacer(modifier = Modifier.height(16.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("WAVEFORM_COMPARE", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    DualWaveformCanvas(
                        waveA = synthA!!.waveformTypes,
                        waveB = synthB!!.waveformTypes,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SPECS_COMPARISON", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    CompareRow("BRAND", synthA!!.brand, synthB!!.brand, SynthCyan, SynthMagenta)
                    CompareRow("CATEGORY", synthA!!.category.take(18), synthB!!.category.take(18), SynthCyan, SynthMagenta)
                    CompareRow("OSCILLATORS", synthA!!.oscillators.take(18), synthB!!.oscillators.take(18), SynthCyan, SynthMagenta)
                    CompareRow("FILTERS", synthA!!.filterTypes.take(18), synthB!!.filterTypes.take(18), SynthCyan, SynthMagenta)
                    CompareRow("WAVEFORMS", synthA!!.waveformTypes.take(18), synthB!!.waveformTypes.take(18), SynthCyan, SynthMagenta)
                    CompareRow("YEAR", "${synthA!!.yearReleased}", "${synthB!!.yearReleased}", SynthCyan, SynthMagenta)
                    CompareRow("POLYPHONY", synthA!!.polyphony, synthB!!.polyphony, SynthCyan, SynthMagenta)
                    CompareRow("SOUND", synthA!!.soundCharacter.take(18), synthB!!.soundCharacter.take(18), SynthCyan, SynthMagenta)
                    CompareRow("BEST_FOR", synthA!!.bestFor.take(18), synthB!!.bestFor.take(18), SynthCyan, SynthMagenta)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.35f,
                cornerRadius = 12.dp
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("VERDICT", color = SynthPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    val verdict = generateVerdict(synthA!!, synthB!!)
                    Text(verdict, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = FontFamily.Monospace, lineHeight = 16.sp)
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Compare, null, tint = DarkOnSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("SELECT TWO SYNTHS", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 12.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
                    Text("Tap the cards above to compare", color = DarkOnSurfaceVariant.copy(alpha = 0.3f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (showPickerA) {
        SynthPickerDialog(
            synths = allSynths,
            onSelect = { synthA = it; showPickerA = false },
            onDismiss = { showPickerA = false },
            accentColor = SynthCyan
        )
    }
    if (showPickerB) {
        SynthPickerDialog(
            synths = allSynths,
            onSelect = { synthB = it; showPickerB = false },
            onDismiss = { showPickerB = false },
            accentColor = SynthMagenta
        )
    }
}

@Composable
private fun SynthSelectorCard(
    label: String,
    color: Color,
    synth: SynthLibraryItem?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassPanel(
        modifier = modifier
            .height(100.dp)
            .clickable { onClick() },
        alpha = if (synth != null) 0.4f else 0.25f,
        cornerRadius = 12.dp,
        glowColor = color,
        glowIntensity = if (synth != null) 0.2f else 0f
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, color = color, fontSize = 9.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            if (synth != null) {
                Text(synth.name, color = DarkOnSurface, fontSize = 12.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, maxLines = 1)
                Text(synth.brand, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
            } else {
                Text("Tap to select", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
private fun CompareRow(label: String, valueA: String, valueB: String, colorA: Color, colorB: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(70.dp))
        Text(valueA, color = colorA.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        Text(valueB, color = colorB.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun DualWaveformCanvas(waveA: String, waveB: String, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val anim by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "anim"
    )

    Canvas(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(DarkSurfaceVariant)) {
        val w = size.width
        val h = size.height
        val cy = h / 2

        val pathA = Path()
        val pathB = Path()

        for (i in 0..100) {
            val x = (i.toFloat() / 100) * w
            val yA = cy + sin(x / w * 4 * PI + anim * PI * 2).toFloat() * (h * 0.3f)
            val yB = cy + cos(x / w * 3 * PI + anim * PI * 2).toFloat() * (h * 0.25f)

            if (i == 0) { pathA.moveTo(x, yA); pathB.moveTo(x, yB) }
            else { pathA.lineTo(x, yA); pathB.lineTo(x, yB) }
        }

        drawPath(pathA, SynthCyan.copy(alpha = 0.6f), style = Stroke(2f))
        drawPath(pathB, SynthMagenta.copy(alpha = 0.6f), style = Stroke(2f))
        drawLine(DarkOnSurfaceVariant.copy(alpha = 0.2f), Offset(0f, cy), Offset(w, cy), 0.5f)
    }
}

@Composable
private fun SynthPickerDialog(
    synths: List<SynthLibraryItem>,
    onSelect: (SynthLibraryItem) -> Unit,
    onDismiss: () -> Unit,
    accentColor: Color
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text("SELECT_SYNTH", color = accentColor, fontSize = 12.sp, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
        },
        text = {
            Column {
                synths.take(30).forEach { synth ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(synth) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(accentColor.copy(alpha = 0.6f))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(synth.name, color = DarkOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Text(synth.brand, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = DarkOnSurfaceVariant, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
            }
        }
    )
}

private fun generateVerdict(a: SynthLibraryItem, b: SynthLibraryItem): String {
    val aAnalog = a.category.contains("Analog", ignoreCase = true)
    val bAnalog = b.category.contains("Analog", ignoreCase = true)
    val aPoly = a.polyphony.contains("Poly", ignoreCase = true)
    val bPoly = b.polyphony.contains("Poly", ignoreCase = true)

    return buildString {
        appendLine("${a.name}: ${a.soundCharacter.ifEmpty { a.category }}")
        appendLine("${b.name}: ${b.soundCharacter.ifEmpty { b.category }}")
        appendLine()
        if (aAnalog && !bAnalog) appendLine("→ ${a.name} offers warmer analog character")
        else if (!aAnalog && bAnalog) appendLine("→ ${b.name} offers warmer analog character")
        if (aPoly && !bPoly) appendLine("→ ${a.name} provides more polyphonic versatility")
        else if (!aPoly && bPoly) appendLine("→ ${b.name} provides more polyphonic versatility")
        appendLine("→ Both excel in ${a.bestFor.ifEmpty { "general synthesis" }}")
    }
}
