package com.synthlens.app

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.data.SynthLibraryItem
import com.synthlens.app.ui.navigation.Screen
import com.synthlens.app.ui.screens.*
import com.synthlens.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*

data class BottomNavItem(
    val screen: Screen,
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SynthLensApp(audioEngine: AudioEngine) {
    var showSplash by remember { mutableStateOf(true) }

    if (showSplash) {
        SynthLensSplash(onFinished = { showSplash = false })
        return
    }

    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Analyzer, Icons.Default.MusicNote, "AUDIO"),
        BottomNavItem(Screen.Scanner, Icons.Default.CameraAlt, "SCAN"),
        BottomNavItem(Screen.Spectral, Icons.Default.Equalizer, "SPECTRUM"),
        BottomNavItem(Screen.Library, Icons.Default.LibraryBooks, "LIBRARY"),
        BottomNavItem(Screen.Profile, Icons.Default.Person, "SPECS")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                DarkBackground.copy(alpha = 0.8f),
                                DarkBackground
                            )
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.08f),
                                    Color.White.copy(alpha = 0.03f)
                                )
                            )
                        )
                        .border(
                            width = 0.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.12f),
                                    Color.White.copy(alpha = 0.04f)
                                )
                            ),
                            shape = RoundedCornerShape(20.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentRoute == item.screen.route
                            GlassNavItem(
                                icon = item.icon,
                                label = item.label,
                                selected = selected,
                                onClick = {
                                    if (currentRoute != item.screen.route) {
                                        navController.navigate(item.screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        var selectedSynth by remember { mutableStateOf<SynthLibraryItem?>(null) }

        if (selectedSynth != null) {
            SynthDetailScreen(
                synth = selectedSynth!!,
                onBack = { selectedSynth = null }
            )
        } else {
            NavHost(
                navController = navController,
                startDestination = Screen.Analyzer.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(Screen.Analyzer.route) {
                    AnalyzerScreen(
                        audioEngine = audioEngine,
                        onNavigateToDetails = {
                            navController.navigate(Screen.AnalysisDetails.route) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(Screen.AnalysisDetails.route) {
                    AnalysisDetailsScreen(
                        audioEngine = audioEngine,
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.History.route) {
                    HistoryScreen()
                }
                composable(Screen.ABCompare.route) {
                    ABCompareScreen(audioEngine = audioEngine)
                }
                composable(Screen.Export.route) {
                    ExportScreen()
                }
                composable(Screen.StageMode.route) {
                    StageModeScreen(audioEngine = audioEngine)
                }
                composable(Screen.DAWIntegration.route) {
                    DAWIntegrationScreen(audioEngine = audioEngine)
                }
                composable(Screen.Scanner.route) {
                    CameraScannerScreen()
                }
                composable(Screen.Spectral.route) {
                    SpectralScreen(audioEngine = audioEngine)
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onSynthSelected = { selectedSynth = it }
                    )
                }
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        onNavigateToHistory = { navController.navigate(Screen.History.route) },
                        onNavigateToABCompare = { navController.navigate(Screen.ABCompare.route) },
                        onNavigateToExport = { navController.navigate(Screen.Export.route) },
                        onNavigateToStageMode = { navController.navigate(Screen.StageMode.route) },
                        onNavigateToDAW = { navController.navigate(Screen.DAWIntegration.route) },
                        onNavigateToScanner = { navController.navigate(Screen.Scanner.route) },
                        onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                        onNavigateToAchievements = { navController.navigate(Screen.Achievements.route) },
                        onNavigateToBatchProcessing = { navController.navigate(Screen.BatchProcessing.route) },
                        onNavigateToCloudWorkspace = { navController.navigate(Screen.CloudWorkspace.route) },
                        onNavigateToHardwareIntegration = { navController.navigate(Screen.HardwareIntegration.route) },
                        onNavigateToQADashboard = { navController.navigate(Screen.QADashboard.route) }
                    )
                }
                composable(Screen.BatchProcessing.route) {
                    BatchProcessingScreen(
                        onBack = { navController.popBackStack() }
                    )
                }
                composable(Screen.CloudWorkspace.route) {
                    CloudWorkspaceScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.HardwareIntegration.route) {
                    HardwareIntegrationScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.QADashboard.route) {
                    QADashboardScreen(onBack = { navController.popBackStack() })
                }
                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
                composable(Screen.Achievements.route) {
                    AchievementsScreen(
                        achievements = emptyList(),
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassNavItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val isBright = LocalIsBright.current
    val boost = if (isBright) 2.5f else 1f
    val shape = RoundedCornerShape(14.dp)
    val bgAlpha by animateFloatAsState(
        targetValue = if (selected) (0.15f * boost).coerceIn(0f, 0.5f) else (0.06f * boost).coerceIn(0f, 0.2f),
        animationSpec = tween(200),
        label = "bgAlpha"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (selected) (0.4f * boost).coerceIn(0f, 1f) else (0.12f * boost).coerceIn(0f, 0.4f),
        animationSpec = tween(200),
        label = "borderAlpha"
    )
    val iconAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else (0.8f * boost).coerceIn(0f, 1f),
        animationSpec = tween(200),
        label = "iconAlpha"
    )

    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (selected)
                    SynthCyan.copy(alpha = bgAlpha)
                else
                    Color.White.copy(alpha = bgAlpha)
            )
            .border(
                width = if (isBright) 1.dp else 0.5.dp,
                color = if (selected)
                    SynthCyan.copy(alpha = borderAlpha)
                else
                    Color.White.copy(alpha = borderAlpha),
                shape = shape
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(if (isBright) 22.dp else 20.dp),
                tint = if (selected) SynthCyan else Color.White.copy(alpha = iconAlpha)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = if (isBright) 10.sp else 9.sp,
                fontWeight = if (selected || isBright) FontWeight.Normal else FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                color = if (selected) SynthCyan else Color.White.copy(alpha = (iconAlpha * 0.85f).coerceIn(0f, 1f))
            )
        }
    }
}

@Composable
private fun SynthLensSplash(onFinished: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "splash")

    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoScale"
    )
    val logoAlpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "logoAlpha"
    )
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "ringRotation"
    )
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing)),
        label = "scanLine"
    )
    val subtitleAlpha by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "subtitleAlpha"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowPulse"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(DarkBackground),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height; val cx = w / 2; val cy = h / 2
            val gridAlpha = 0.03f + logoAlpha * 0.02f
            for (i in 0..10) { drawLine(SynthCyan.copy(alpha = gridAlpha), Offset(0f, h * i / 10), Offset(w, h * i / 10), 0.5f) }
            for (i in 0..8) { drawLine(SynthCyan.copy(alpha = gridAlpha * 0.7f), Offset(w * i / 8, 0f), Offset(w * i / 8, h), 0.5f) }
            val coreR = minOf(w, h) * 0.08f * logoScale
            val outerGlow = Brush.radialGradient(listOf(SynthCyan.copy(alpha = glowPulse * 0.15f), SynthMagenta.copy(alpha = glowPulse * 0.08f), Color.Transparent), Offset(cx, cy), coreR * 4f)
            drawCircle(outerGlow, coreR * 4f, Offset(cx, cy))
            drawCircle(SynthCyan.copy(alpha = 0.08f), coreR * 2.5f, Offset(cx, cy))
            drawCircle(SynthMagenta.copy(alpha = 0.05f), coreR * 2f, Offset(cx, cy))
            drawCircle(SynthCyan.copy(alpha = 0.15f + glowPulse * 0.1f), coreR * 1.8f, Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(1f))
            drawCircle(SynthMagenta.copy(alpha = 0.1f), coreR * 1.4f, Offset(cx, cy), style = androidx.compose.ui.graphics.drawscope.Stroke(0.8f))
            val coreGradient = Brush.radialGradient(listOf(Color.White.copy(alpha = 0.2f + glowPulse * 0.15f), SynthCyan.copy(alpha = 0.3f), SynthMagenta.copy(alpha = 0.15f)), Offset(cx, cy), coreR)
            drawCircle(coreGradient, coreR * logoScale, Offset(cx, cy))
            for (i in 0..8) {
                val angle = (i * 40f + ringRotation) * PI.toFloat() / 180f
                val orbitR = coreR * (2f + sin(angle * 0.5f) * 0.3f)
                val px = cx + cos(angle) * orbitR; val py = cy + sin(angle) * orbitR
                val dotAlpha = (0.3f + glowPulse * 0.3f * abs(sin(angle * 2f))).coerceIn(0.15f, 0.7f)
                val dotColor = when (i % 3) { 0 -> SynthCyan; 1 -> SynthMagenta; else -> SynthPurple }
                drawCircle(dotColor.copy(alpha = dotAlpha * 0.3f), 6f, Offset(px, py))
                drawCircle(dotColor.copy(alpha = dotAlpha), 2.5f, Offset(px, py))
            }
            drawLine(SynthCyan.copy(alpha = 0.08f + glowPulse * 0.05f), Offset(scanLineY * w, 0f), Offset(scanLineY * w, h), 1f)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.graphicsLayer { alpha = logoAlpha }) {
            Spacer(modifier = Modifier.height(120.dp))
            Text("SYNTH", color = SynthCyan, fontSize = 42.sp, fontWeight = FontWeight.Thin, fontFamily = FontFamily.Monospace, letterSpacing = 12.sp)
            Text("LENS", color = SynthMagenta, fontSize = 42.sp, fontWeight = FontWeight.Thin, fontFamily = FontFamily.Monospace, letterSpacing = 12.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.width(60.dp).height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, SynthCyan.copy(alpha = 0.5f), Color.Transparent))))
            Spacer(modifier = Modifier.height(16.dp))
            Text("SYNTH DETECTION ENGINE", color = SynthCyan.copy(alpha = subtitleAlpha), fontSize = 10.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("v2.0 // STEM SEPARATION // ML READY", color = SynthMagenta.copy(alpha = subtitleAlpha * 0.7f), fontSize = 8.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
        }
    }
}
