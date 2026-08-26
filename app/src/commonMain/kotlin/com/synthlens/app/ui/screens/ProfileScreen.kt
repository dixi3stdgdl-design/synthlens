package com.synthlens.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.synthlens.app.viewmodel.createSynthViewModel
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel

@Composable
fun ProfileScreen(
    viewModel: SynthViewModel = createSynthViewModel(),
    onNavigateToHistory: () -> Unit = {},
    onNavigateToABCompare: () -> Unit = {},
    onNavigateToExport: () -> Unit = {},
    onNavigateToStageMode: () -> Unit = {},
    onNavigateToDAW: () -> Unit = {},
    onNavigateToScanner: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToBatchProcessing: () -> Unit = {},
    onNavigateToCloudWorkspace: () -> Unit = {},
    onNavigateToHardwareIntegration: () -> Unit = {},
    onNavigateToQADashboard: () -> Unit = {},
    onNavigateToSubscription: () -> Unit = {}
) {
    val detectedCount by viewModel.detectedCount.collectAsState()
    val totalCount by viewModel.totalCount.collectAsState()
    val detectedSynths by viewModel.detectedSynths.collectAsState()
    val allSynths by viewModel.allSynths.collectAsState()

    val detectedBrands = remember(detectedSynths) {
        detectedSynths.map { it.brand }.toSet()
    }

    val infiniteTransition = rememberInfiniteTransition()
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse)
    )

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
            Text("SYSTEM_SPECS", color = SynthCyan, fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace, letterSpacing = 3.sp)
            Text("v2.0", color = SynthAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.4f, cornerRadius = 12.dp, glowColor = SynthCyan, glowIntensity = 0.15f) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("TECHNICAL_MANIFEST", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SpecManifestItem("TOTAL_UNITS", "$totalCount", Modifier.weight(1f))
                    SpecManifestItem("DETECTED", "$detectedCount", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SpecManifestItem("BRANDS", "${detectedBrands.size}", Modifier.weight(1f))
                    val detectionRate = if (totalCount > 0) (detectedCount * 100 / totalCount) else 0
                    SpecManifestItem("SCAN_RATE", "${detectionRate}%", Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("TOOLS_AND_FEATURES", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))

                FeatureButton("DETECTION_LOG", "View all past detections", Icons.Default.History, SynthAmber, onNavigateToHistory)
                FeatureButton("ACHIEVEMENTS", "View your achievements and progress", Icons.Default.EmojiEvents, SynthGreen, onNavigateToAchievements)
                FeatureButton("A/B_COMPARE", "Compare two synths side by side", Icons.Default.Compare, SynthCyan, onNavigateToABCompare)
                FeatureButton("EXPORT_SHARE", "Share detections to social media", Icons.Default.Share, SynthGreen, onNavigateToExport)
                FeatureButton("STAGE_MODE", "Large display for live performance", Icons.Default.RocketLaunch, SynthMagenta, onNavigateToStageMode)
                FeatureButton("BATCH_PROCESSING", "Offline audio processing and dataset generation", Icons.Default.Audiotrack, SynthCyan, onNavigateToBatchProcessing)
                FeatureButton("DAW_INTEGRATION", "MIDI/OSC output for your DAW", Icons.Default.SettingsInputHdmi, SynthPurple, onNavigateToDAW)
                FeatureButton("CAMERA_SCANNER", "Visual synth detection via camera", Icons.Default.CameraAlt, SynthCyan, onNavigateToScanner)
                FeatureButton("SETTINGS", "App configuration", Icons.Default.Tune, DarkOnSurfaceVariant, onNavigateToSettings)
                FeatureButton("UPGRADE_SYNTHLENS", "Unlock Pro and Enterprise features", Icons.Default.Star, SynthAmber, onNavigateToSubscription)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("DETECTION_PROGRESS", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("SCANNED: $detectedCount/$totalCount", color = DarkOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                    Text("${if (totalCount > 0) detectedCount * 100 / totalCount else 0}%", color = SynthGreen, fontSize = 14.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(DarkSurfaceVariant)) {
                    Box(modifier = Modifier.fillMaxWidth(fraction = if (totalCount > 0) detectedCount.toFloat() / totalCount else 0f).fillMaxHeight().clip(RoundedCornerShape(3.dp)).background(SynthGreen.copy(alpha = 0.7f * pulse)))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("HARDWARE_LIBRARY", color = SynthMagenta, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                val brandCounts = allSynths.groupBy { it.brand }
                brandCounts.forEach { (brand, synths) ->
                    val detectedInBrand = synths.count { it.isDetected }
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(brand.uppercase(), color = DarkOnSurfaceVariant.copy(alpha = 0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(0.4f))
                        Box(modifier = Modifier.weight(0.4f).height(4.dp).clip(RoundedCornerShape(2.dp)).background(DarkSurfaceVariant)) {
                            Box(modifier = Modifier.fillMaxWidth(fraction = if (synths.isNotEmpty()) detectedInBrand.toFloat() / synths.size else 0f).fillMaxHeight().clip(RoundedCornerShape(2.dp)).background(SynthCyan.copy(alpha = 0.5f)))
                        }
                        Text("$detectedInBrand/${synths.size}", color = SynthCyan.copy(alpha = 0.6f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(start = 8.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onNavigateToBatchProcessing() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SynthCyan)
                ) {
                    Icon(Icons.Default.LibraryBooks, contentDescription = null, tint = DarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("BATCH AUDIO PROCESSING", color = DarkBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateToCloudWorkspace() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SynthPurple)
                ) {
                    Icon(Icons.Default.Cloud, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CLOUD WORKSPACE SYNC", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateToHardwareIntegration() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SynthMagenta)
                ) {
                    Icon(Icons.Default.Cable, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("HARDWARE MIDI LINK", color = Color.White, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onNavigateToQADashboard() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.8f))
                ) {
                    Icon(Icons.Default.FactCheck, contentDescription = null, tint = DarkBackground)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("QA SDK DASHBOARD", color = DarkBackground, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("RECENT_DETECTIONS", color = SynthPurple, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                if (detectedSynths.isEmpty()) {
                    Text("NO_DETECTIONS_YET", color = DarkOnSurfaceVariant.copy(alpha = 0.3f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                } else {
                    detectedSynths.take(5).forEach { synth ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(SynthGreen.copy(alpha = 0.6f)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("${synth.brand} ${synth.name}", color = DarkOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("${synth.category} // ${synth.waveformType}", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            }
                            Text(String.format("%.0f%%", synth.confidence * 100), color = SynthGreen.copy(alpha = 0.7f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        GlassPanel(modifier = Modifier.fillMaxWidth(), alpha = 0.35f, cornerRadius = 12.dp) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("APP_INFO", color = DarkOnSurfaceVariant.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(10.dp))
                InfoRow("VERSION", "2.0.0")
                InfoRow("ENGINE", "AUDIO_ANALYSIS_V3_ML")
                InfoRow("DETECTION", "${totalCount}_SYNTH_PROFILES")
                InfoRow("LIBRARY", "${totalCount}_ENTRIES")
                InfoRow("STEM_ENGINE", "4-BAND SEPARATION")
                InfoRow("ML_STATUS", "HEURISTIC + TFLITE READY")
                InfoRow("STATUS", "OPERATIONAL")
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun FeatureButton(title: String, subtitle: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.03f))
            .clickable { onClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = DarkOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium)
            Text(subtitle, color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        }
        Icon(Icons.Default.ChevronRight, null, tint = DarkOnSurfaceVariant.copy(alpha = 0.3f), modifier = Modifier.size(16.dp))
    }
    Spacer(modifier = Modifier.height(4.dp))
}

@Composable
private fun SpecManifestItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(end = 8.dp)) {
        Text("$label:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 7.sp, fontFamily = FontFamily.Monospace, letterSpacing = 1.sp)
        Text(value, color = DarkOnSurface, fontSize = 14.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = DarkOnSurface.copy(alpha = 0.8f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}
