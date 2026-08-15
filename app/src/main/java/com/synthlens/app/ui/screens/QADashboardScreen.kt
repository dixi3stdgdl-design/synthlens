package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.ui.theme.DarkBackground
import com.synthlens.app.ui.theme.SynthCyan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QADashboardScreen(onBack: () -> Unit) {
    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text("QA DASHBOARD", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp) 
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
            Icon(Icons.Default.FactCheck, contentDescription = null, tint = Color.Green.copy(alpha = 0.8f), modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Assembly Line QA SDK",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Real-time acoustic evaluations based on historical workspace runs.",
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            val context = androidx.compose.ui.platform.LocalContext.current
            val repo = androidx.compose.runtime.remember { com.synthlens.app.data.CloudWorkspaceRepository(context) }
            val patches by repo.syncWorkspace().collectAsState(initial = emptyList())
            
            val totalTested = patches.size
            val passed = patches.count { it.confidence >= 0.8f }
            val failed = totalTested - passed
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatBox("UNITS TESTED", totalTested.toString(), SynthCyan)
                StatBox("PASSED", passed.toString(), Color.Green.copy(alpha = 0.8f))
                StatBox("FAILED", failed.toString(), Color.Red.copy(alpha = 0.8f))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text("Latest Failures", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            
            val failedPatches = patches.filter { it.confidence < 0.8f }.sortedByDescending { it.timestamp }.take(3)
            
            if (failedPatches.isEmpty()) {
                Text("No failures detected in local history.", color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            } else {
                failedPatches.forEach { patch ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text("Unit ID: ${patch.id}", color = Color.Red.copy(alpha = 0.8f), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Failed Metric: Low Confidence (${(patch.confidence * 100).toInt()}%)", color = Color.White.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                        val df = java.text.SimpleDateFormat("MMM dd, HH:mm", java.util.Locale.US)
                        Text("Timestamp: ${df.format(java.util.Date(patch.timestamp))}", color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text(label, color = Color.White.copy(alpha = 0.6f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
    }
}
