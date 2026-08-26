package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.synthlens.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchProcessingScreen(onBack: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var logs by remember { mutableStateOf(listOf<String>()) }
    var results by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Batch Processing", color = DarkOnSurface) },
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
                .padding(padding)
                .padding(16.dp)
        ) {
            // Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Folder, contentDescription = null, tint = SynthCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Automated Audio Tagging", color = DarkOnSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Select a folder containing WAV/MP3 files. SynthLens will analyze each file headlessly and tag the detected synth model.", 
                        color = DarkOnSurfaceVariant, fontSize = 14.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            if (!isProcessing) {
                                isProcessing = true
                                logs = listOf("Scanning folder...")
                                results = emptyList()
                                progress = 0f
                                
                                coroutineScope.launch {
                                    delay(800)
                                    val fakeFiles = listOf("bass_drop.wav", "lead_arp.wav", "pad_warm.wav", "kick_909.wav", "fx_riser.wav")
                                    logs = logs + "Found ${fakeFiles.size} audio files."
                                    
                                    for ((index, file) in fakeFiles.withIndex()) {
                                        delay(600) // Simulate processing time
                                        progress = (index + 1).toFloat() / fakeFiles.size
                                        val detected = when(file) {
                                            "bass_drop.wav" -> "Moog Minimoog Model D"
                                            "lead_arp.wav" -> "Roland Juno-106"
                                            "pad_warm.wav" -> "Sequential Prophet-5"
                                            "kick_909.wav" -> "Roland TR-909"
                                            else -> "Unknown Synth"
                                        }
                                        results = results + Pair(file, detected)
                                        logs = logs + "Processed $file -> $detected"
                                    }
                                    
                                    delay(500)
                                    isProcessing = false
                                    logs = logs + "Batch processing complete."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthCyan, contentColor = Color.Black),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Processing...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SELECT FOLDER", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (isProcessing || progress > 0f) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = SynthCyan,
                    trackColor = DarkSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Results List
            Text("Processing Results", color = DarkOnSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                if (results.isEmpty() && !isProcessing) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No files processed yet.", color = DarkOnSurfaceVariant)
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(results.size) { index ->
                            val result = results[index]
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.AudioFile, contentDescription = null, tint = DarkOnSurfaceVariant, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(result.first, color = DarkOnSurface, fontSize = 14.sp)
                                    Text("Tags: ${result.second}", color = SynthAmber, fontSize = 12.sp)
                                }
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SynthGreen, modifier = Modifier.size(16.dp))
                            }
                            if (index < results.size - 1) {
                                HorizontalDivider(color = DarkBorder, modifier = Modifier.padding(horizontal = 8.dp))
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (results.isNotEmpty() && !isProcessing) {
                OutlinedButton(
                    onClick = { /* TODO: Export CSV */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SynthCyan),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SynthCyan)
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXPORT CSV REPORT", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
