package com.synthlens.app.ui.screens

import android.media.midi.MidiDeviceInfo
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Send
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
import com.synthlens.app.engine.midi.MidiController
import com.synthlens.app.ui.theme.DarkBackground
import com.synthlens.app.ui.theme.SynthCyan
import com.synthlens.app.ui.theme.SynthMagenta

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardwareIntegrationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val midiController = remember { MidiController(context) }
    
    var devices by remember { mutableStateOf(emptyList<MidiDeviceInfo>()) }
    
    LaunchedEffect(Unit) {
        devices = midiController.getConnectedDevices()
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text("HARDWARE MIDI LINK", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Cable, contentDescription = null, tint = SynthMagenta, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Bi-directional Hardware Control",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Push ML-detected patches directly to connected USB/BLE synthesizers via SysEx or Control Change messages.",
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (devices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No MIDI devices connected. Please connect via USB-OTG or BLE.",
                        color = Color.White.copy(alpha = 0.5f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(devices) { device ->
                        var name = "Unknown Device"
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            name = device.properties.getString(MidiDeviceInfo.PROPERTY_NAME) ?: "Unknown Device"
                        }
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(name, color = SynthCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Input Ports: ${device.inputPortCount}", color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                            
                            if (device.inputPortCount > 0) {
                                IconButton(
                                    onClick = { 
                                        midiController.pushPatchToDevice(device, "Current ML Patch", emptyMap())
                                    }
                                ) {
                                    Icon(Icons.Default.Send, contentDescription = "Push Patch", tint = SynthMagenta)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
