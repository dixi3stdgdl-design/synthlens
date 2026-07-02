package com.example.myapplication.ui.screens

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.*
import com.example.myapplication.ui.theme.*

@Composable
fun SettingsScreen() {
    var selectedSampleRate by remember { mutableStateOf("44100") }
    var selectedBufferSize by remember { mutableStateOf("2048") }
    var stemSeparation by remember { mutableStateOf(true) }
    var autoDetect by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("SETTINGS", color = SynthCyan, fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace, letterSpacing = 3.sp)

        Spacer(modifier = Modifier.height(16.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("AUDIO_ENGINE", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                SettingsOption("Sample Rate", selectedSampleRate, listOf("22050", "44100", "48000")) { selectedSampleRate = it }
                SettingsOption("Buffer Size", selectedBufferSize, listOf("1024", "2048", "4096")) { selectedBufferSize = it }
                SettingsToggle("Stem Separation", stemSeparation) { stemSeparation = it }
                SettingsToggle("Auto-Detect", autoDetect) { autoDetect = it }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("DISPLAY", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                SettingsToggle("Dark Mode", darkMode) { darkMode = it }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("ABOUT", color = SynthMagenta, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                InfoRowSettings("App Name", "SynthLens")
                InfoRowSettings("Version", "2.0.0")
                InfoRowSettings("Engine", "Audio Analysis V3 + ML")
                InfoRowSettings("DB Version", "3 (Smart Migrations)")
                InfoRowSettings("Synths", "80+ Profiles")
                InfoRowSettings("Stem Bands", "4 (Sub/Bass/Mid/High)")
                InfoRowSettings("Platform", "Android (minSdk 33)")
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun SettingsOption(label: String, currentValue: String, options: List<String>, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        options.forEach { option ->
            GlassChip(
                modifier = Modifier.padding(start = 4.dp),
                isSelected = option == currentValue
            ) {
                Box(modifier = Modifier.clickable { onSelect(option) }.padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(option, color = if (option == currentValue) SynthCyan else DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

@Composable
private fun SettingsToggle(label: String, enabled: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = SynthCyan,
                checkedTrackColor = SynthCyan.copy(alpha = 0.3f),
                uncheckedThumbColor = DarkOnSurfaceVariant,
                uncheckedTrackColor = DarkSurfaceVariant
            )
        )
    }
}

@Composable
private fun InfoRowSettings(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}
