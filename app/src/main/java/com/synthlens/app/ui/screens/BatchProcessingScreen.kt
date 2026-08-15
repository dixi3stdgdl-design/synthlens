package com.synthlens.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
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
import com.synthlens.app.engine.BatchAudioProcessor
import com.synthlens.app.engine.SynthMLClassifier
import com.synthlens.app.ui.theme.DarkBackground
import com.synthlens.app.ui.theme.SynthCyan
import com.synthlens.app.ui.theme.SynthMagenta
import kotlinx.coroutines.launch
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchProcessingScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Lazy initialization of Processor to ensure Context is available
    val processor = remember {
        val classifier = SynthMLClassifier(context)
        BatchAudioProcessor(context, classifier)
    }

    val isProcessing by processor.isProcessing.collectAsState()
    val progress by processor.progress.collectAsState()
    val results by processor.results.collectAsState()
    
    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedUris = uris
        }
    }

    val csvExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        uri?.let {
            scope.launch {
                try {
                    context.contentResolver.openOutputStream(it)?.use { os ->
                        val writer = OutputStreamWriter(os)
                        writer.write("File Name,Detected Synth,Confidence,Category,Engine,Details\n")
                        results.forEach { res ->
                            val classification = res.classification
                            if (classification != null) {
                                writer.write("${res.fileName},${classification.synthName},${classification.confidence},${classification.category},${classification.modelUsed},\"${res.error ?: ""}\"\n")
                            } else {
                                writer.write("${res.fileName},Unknown,0.0,,,\"${res.error ?: ""}\"\n")
                            }
                        }
                        writer.flush()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "BATCH PROCESSING", 
                        color = Color.White, 
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp,
                        letterSpacing = 2.sp
                    ) 
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
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            
            Text(
                "Select WAV files for offline ML processing and training set generation.",
                color = Color.White.copy(alpha = 0.7f),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { filePickerLauncher.launch(arrayOf("audio/wav", "audio/x-wav")) },
                    colors = ButtonDefaults.buttonColors(containerColor = SynthCyan.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.Audiotrack, contentDescription = null, tint = SynthCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SELECT FILES", color = SynthCyan, fontFamily = FontFamily.Monospace)
                }

                Button(
                    onClick = { 
                        scope.launch { processor.processBatch(selectedUris) } 
                        // Production fallback: Enqueue to WorkManager to survive app death
                        val workRequest = androidx.work.OneTimeWorkRequestBuilder<com.synthlens.app.engine.workers.BatchProcessingWorker>()
                            .setInputData(androidx.work.workDataOf("URI_LIST" to selectedUris.map { it.toString() }.toTypedArray()))
                            .build()
                        androidx.work.WorkManager.getInstance(context).enqueue(workRequest)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SynthMagenta.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isProcessing && selectedUris.isNotEmpty()
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = SynthMagenta)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("START BATCH", color = SynthMagenta, fontFamily = FontFamily.Monospace)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isProcessing) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = SynthCyan,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Processing... ${(progress * 100).toInt()}%",
                    color = SynthCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            } else if (results.isNotEmpty()) {
                Button(
                    onClick = { 
                        val df = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                        val timestamp = df.format(Date())
                        csvExportLauncher.launch("synthlens_batch_$timestamp.csv") 
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EXPORT CSV REPORT", color = Color.White, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayList = if (results.isNotEmpty()) results else selectedUris.map { com.synthlens.app.engine.BatchResult(it, it.lastPathSegment ?: "Unknown", null) }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(displayList) { item ->
                    BatchResultRow(item)
                    Divider(color = Color.White.copy(alpha = 0.05f))
                }
            }
        }
    }
}

@Composable
fun BatchResultRow(result: com.synthlens.app.engine.BatchResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Text(
            text = result.fileName,
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        if (result.classification != null) {
            val confidence = (result.classification.confidence * 100).toInt()
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Detected: ${result.classification.synthName}",
                    color = SynthCyan,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
                Text(
                    text = "$confidence%",
                    color = if (confidence > 80) Color.Green else SynthMagenta,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
        } else if (result.error != null) {
             Text(
                text = "Error: ${result.error}",
                color = Color.Red.copy(alpha = 0.8f),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        } else {
             Text(
                text = "Pending...",
                color = Color.White.copy(alpha = 0.5f),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
