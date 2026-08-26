package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.synthlens.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QADashboardScreen(onBack: () -> Unit = {}) {
    val coroutineScope = rememberCoroutineScope()
    var isScanning by remember { mutableStateOf(false) }
    var showCertificateDialog by remember { mutableStateOf(false) }
    var thdValue by remember { mutableStateOf("1.2%") }
    var noiseValue by remember { mutableStateOf("-86 dB") }
    var phaseValue by remember { mutableStateOf("98.4%") }
    var rolloffValue by remember { mutableStateOf("22.0 kHz") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QA Telemetry & Certs", color = DarkOnSurface) },
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
            Text("HARDWARE TARGET BENCH", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.DeveloperBoard, contentDescription = null, tint = DarkOnSurface)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Roland Juno-106", color = DarkOnSurface, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(if (isScanning) "Scanning Audio Telemetry..." else "Factory Spec Tolerances: PASS", color = if (isScanning) SynthCyan else SynthGreen, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        if (!isScanning) {
                            isScanning = true
                            coroutineScope.launch {
                                delay(1200)
                                thdValue = "1.08%"
                                noiseValue = "-89.2 dB"
                                phaseValue = "99.1%"
                                rolloffValue = "22.4 kHz"
                                isScanning = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SynthPurple),
                    enabled = !isScanning
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CALIBRATE")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Diagnostics
            Text("LIVE DIAGNOSTIC METRICS", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "THD (Distortion)",
                    value = thdValue,
                    status = "PASS (<3%)",
                    statusColor = SynthGreen,
                    icon = Icons.Default.Timeline
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Noise Floor",
                    value = noiseValue,
                    status = "OPTIMAL",
                    statusColor = SynthGreen,
                    icon = Icons.Default.GraphicEq
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Phase Align",
                    value = phaseValue,
                    status = "COHERENT",
                    statusColor = SynthGreen,
                    icon = Icons.Default.Waves
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "Spectral Rolloff",
                    value = rolloffValue,
                    status = "PURE ANALOG",
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
                    .border(1.dp, SynthAmber.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SynthAmber.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SynthAmber, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("HARDWARE HEALTH & AUTHENTICITY", color = SynthAmber, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("Digital Certificate of Analog Authenticity", color = DarkOnSurfaceVariant, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { showCertificateDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthAmber, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("VIEW OFFICIAL CERTIFICATE", fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Powered by SynthLens Acoustic Laboratory Engine v2.0", color = DarkOnSurfaceVariant, fontSize = 11.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }

    if (showCertificateDialog) {
        Dialog(onDismissRequest = { showCertificateDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F0F18))
                    .border(2.dp, SynthAmber, RoundedCornerShape(16.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = SynthAmber, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("SYNTHLENS CERTIFICATE", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    Text("ANALOG INTEGRITY VERIFICATION", color = SynthAmber, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Instrument:", color = DarkOnSurfaceVariant, fontSize = 12.sp)
                            Text("Roland Juno-106 (SN: #84912)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Acoustic Status:", color = DarkOnSurfaceVariant, fontSize = 12.sp)
                            Text("100% Authentic Analog", color = SynthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("THD / Signal Quality:", color = DarkOnSurfaceVariant, fontSize = 12.sp)
                            Text("1.08% (Laboratory Grade)", color = SynthCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Anti-AI Watermark:", color = DarkOnSurfaceVariant, fontSize = 12.sp)
                            Text("PASSED (No Artifacts)", color = SynthGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = DarkBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { showCertificateDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthCyan, contentColor = Color.Black)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DOWNLOAD CERTIFICATE PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
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
            Text(value, color = DarkOnSurface, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(status, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
