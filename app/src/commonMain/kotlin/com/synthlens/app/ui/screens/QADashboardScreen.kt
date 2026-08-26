package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QADashboardScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QA Dashboard", color = DarkOnSurface) },
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
            
            // Current Device Target
            Text("HARDWARE TARGET", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = DarkOnSurface)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Roland Juno-106", color = DarkOnSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Status: Calibrating...", color = SynthAmber, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { /* Simulate Scan */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SynthPurple)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SCAN")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Diagnostics
            Text("LIVE DIAGNOSTICS", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // THD Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "THD",
                    value = "1.2%",
                    status = "PASS",
                    statusColor = SynthGreen,
                    icon = Icons.Default.Timeline
                )
                // Noise Floor Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Noise Floor",
                    value = "-86 dB",
                    status = "WARN",
                    statusColor = SynthAmber,
                    icon = Icons.Default.GraphicEq
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                // Phase Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Phase Align",
                    value = "98.4%",
                    status = "PASS",
                    statusColor = SynthGreen,
                    icon = Icons.Default.Waves
                )
                // Spectral Rolloff Card
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Rolloff",
                    value = "22 kHz",
                    status = "PASS",
                    statusColor = SynthGreen,
                    icon = Icons.Default.StackedLineChart
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authentic Certification
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkCard)
                    .border(1.dp, SynthCyan, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SynthCyan, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ANALOG AUTHENTIC", color = SynthCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("No AI Watermarks Detected", color = DarkOnSurfaceVariant, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { /* Simulate PDF Export */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthCyan, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("GENERATE CERTIFICATE (PDF)", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("NOTE: Certificate generation requires Enterprise license.", color = DarkOnSurfaceVariant, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

@Composable
fun MetricCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    status: String,
    statusColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceVariant)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = DarkOnSurfaceVariant, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = DarkOnSurfaceVariant, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = DarkOnSurface, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(status, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
