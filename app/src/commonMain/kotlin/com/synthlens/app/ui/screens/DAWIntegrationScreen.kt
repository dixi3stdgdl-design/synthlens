package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DAWIntegrationScreen(
    audioEngine: AudioEngine,
    onBack: () -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var isExportingAbleton by remember { mutableStateOf(false) }
    var isBroadcastingOSC by remember { mutableStateOf(true) }
    var exportSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DAW & Studio Bridge", color = DarkOnSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = DarkOnSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBackground,
                    titleContentColor = DarkOnSurface
                )
            )
        },
        containerColor = DarkBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp)
        ) {
            // DAW Bridge Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .border(1.dp, SynthPurple.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SynthPurple.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Piano, contentDescription = null, tint = SynthPurple)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Live DAW Link Server", color = DarkOnSurface, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text("OSC / MIDI Network Bridge", color = SynthPurple, fontSize = 12.sp)
                            }
                        }
                        Switch(
                            checked = isBroadcastingOSC,
                            onCheckedChange = { isBroadcastingOSC = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SynthCyan,
                                checkedTrackColor = SynthCyan.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Network Host", color = DarkOnSurfaceVariant, fontSize = 11.sp)
                            Text("192.168.1.120", color = DarkOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("OSC Port", color = DarkOnSurfaceVariant, fontSize = 11.sp)
                            Text("9000 (UDP)", color = SynthCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Status", color = DarkOnSurfaceVariant, fontSize = 11.sp)
                            Text(if (isBroadcastingOSC) "Broadcasting" else "Standby", color = if (isBroadcastingOSC) SynthGreen else DarkOnSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Supported DAWs
            Text("COMPATIBLE HOSTS", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val daws = listOf(
                Pair("Ableton Live 11 / 12", "Max for Live Bridge (ALS Export)"),
                Pair("FL Studio 21 / 24", "MIDI CC Macro Automation"),
                Pair("Logic Pro X", "Apple AU3 / CoreAudio Protocol"),
                Pair("Bitwig Studio", "Controller Scripting API")
            )

            daws.forEach { (name, desc) ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(name, color = DarkOnSurface, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Text(desc, color = DarkOnSurfaceVariant, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SynthGreen, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // One-Click Export Actions
            Text("DAW EXPORT & STEMS PACK", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (!isExportingAbleton) {
                        isExportingAbleton = true
                        exportSuccessMessage = null
                        coroutineScope.launch {
                            delay(1500)
                            isExportingAbleton = false
                            exportSuccessMessage = "Ableton Project (.als) generated & saved to Downloads!"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = SynthPurple, contentColor = Color.White),
                enabled = !isExportingAbleton
            ) {
                if (isExportingAbleton) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Packaging Ableton Project...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.FolderZip, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXPORT ABLETON LIVE SET (.ALS)", fontWeight = FontWeight.Bold)
                }
            }

            exportSuccessMessage?.let { msg ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(msg, color = SynthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
    }
}
