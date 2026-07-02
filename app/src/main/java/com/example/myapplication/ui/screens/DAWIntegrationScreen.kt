package com.example.myapplication.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.engine.AudioEngine
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*

@Composable
fun DAWIntegrationScreen(audioEngine: AudioEngine) {
    val context = LocalContext.current
    val analysis by audioEngine.analysis.collectAsState()
    val isRecording by audioEngine.isRecording.collectAsState()
    val detected = analysis.detectedSynth

    var midiNote by remember { mutableStateOf(0) }
    var midiChannel by remember { mutableStateOf(0) }

    val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

    LaunchedEffect(analysis.frequency) {
        if (analysis.frequency > 20f) {
            midiNote = (69 + 12 * kotlin.math.log2(analysis.frequency / 440f)).toInt().coerceIn(0, 127)
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
                "DAW_INTEGRATION",
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
            Text(
                "MIDI_OSC",
                color = SynthMagenta,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.4f,
            cornerRadius = 12.dp,
            glowColor = SynthCyan,
            glowIntensity = if (isRecording) 0.2f else 0f
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("LIVE_MIDI_OUTPUT", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("NOTE:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        val noteName = if (analysis.frequency > 20f) {
                            val note = midiNote % 12
                            val octave = (midiNote / 12) - 1
                            "${noteNames[note]}$octave"
                        } else "---"
                        Text(noteName, color = SynthCyan, fontSize = 28.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("MIDI_NOTE:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        Text("$midiNote", color = SynthMagenta, fontSize = 28.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("VELOCITY:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        val velocity = (analysis.amplitude * 127).toInt().coerceIn(0, 127)
                        Text("$velocity", color = SynthAmber, fontSize = 28.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassChip(modifier = Modifier.weight(1f)) {
                        Text("CH: ${midiChannel + 1}", color = DarkOnSurface, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    GlassChip(modifier = Modifier.weight(1f)) {
                        Text("FREQ: ${String.format("%.1f", analysis.frequency)}Hz", color = DarkOnSurface, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    GlassChip(modifier = Modifier.weight(1f)) {
                        Text("WAVE: ${analysis.waveformType}", color = DarkOnSurface, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
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
                Text("DETECTED_SYNTH_PROFILE", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))

                if (detected != null) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestColDAW("SYNTH", "${detected.brand} ${detected.name}", Modifier.weight(1f))
                        ManifestColDAW("FILTER", detected.filterType.take(20), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestColDAW("OSC", detected.oscillators.take(25), Modifier.weight(1f))
                        ManifestColDAW("MOD", detected.modulation.take(20), Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        ManifestColDAW("FX", detected.effects.take(25), Modifier.weight(1f))
                        ManifestColDAW("PATTERN", detected.pattern.take(20), Modifier.weight(1f))
                    }
                } else {
                    Text("AWAITING_SYNTH_DETECTION", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
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
                Text("OSC_PROTOCOL", color = SynthPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("SynthLens can send OSC messages to compatible DAWs:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                Spacer(modifier = Modifier.height(6.dp))

                listOf(
                    "Ableton Live" to "via Max for Live + OSC",
                    "FL Studio" to "via OSC Controller plugin",
                    "Logic Pro" to "via OSC receive in Environment",
                    "Reaper" to "via OSC receptor",
                    "Bitwig Studio" to "native OSC support"
                ).forEach { (daw, method) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(5.dp).clip(RoundedCornerShape(3.dp)).background(SynthPurple.copy(alpha = 0.6f)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(daw, color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.4f))
                        Text(method, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.6f))
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
                Text("MIDI_CC_MAP", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))

                listOf(
                    "CC 1" to "Mod Wheel → Filter Cutoff",
                    "CC 7" to "Volume → Amplitude",
                    "CC 10" to "Pan → Stereo Width",
                    "CC 11" to "Expression → Harmonics",
                    "CC 74" to "Brightness → Spectral Rolloff"
                ).forEach { (cc, mapping) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    ) {
                        Text(cc, color = SynthGreen, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(50.dp))
                        Text(mapping, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ManifestColDAW(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 8.dp)) {
        Text("$label:", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
        Text(value, color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
    }
}
