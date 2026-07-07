package com.synthlens.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.util.Size
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Size as GeoSize
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.synthlens.app.engine.AudioEngine
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import kotlin.math.*

@Composable
fun CameraScannerScreen(
    audioEngine: AudioEngine,
    onSynthDetected: (String) -> Unit
) {
    val analysis by audioEngine.analysis.collectAsState()
    val isRecording by audioEngine.isRecording.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCameraPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    val infiniteTransition = rememberInfiniteTransition()
    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val cornerPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val targetAcquired by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val detected = analysis.detectedSynth
    val modelMatch = if (detected != null) (detected.confidence * 100).toInt() else 0
    val isTargetAcquired = detected != null && detected.confidence > 0.3f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isRecording) SynthGreen else SynthRed)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "OSC_SCANNER_V1",
                    color = DarkOnSurface,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SignalCellularAlt,
                    null,
                    tint = SynthGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "LINK_STRENGTH",
                    color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.4f,
                cornerRadius = 10.dp
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        "SIGNAL_FREQ",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        String.format("%.2f Hz", analysis.frequency),
                        color = SynthGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.4f,
                cornerRadius = 10.dp
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        "WAVEFORM_ANALYSIS",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    if (analysis.frequency > 0) {
                        Text(
                            "OSC_1: ${analysis.waveformType.uppercase()}",
                            color = SynthCyan,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            "FILTER: ${analysis.detectedSynth?.filterType?.take(20) ?: "N/A"}",
                            color = SynthAmber.copy(alpha = 0.7f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        Text(
                            "AWAITING_SIGNAL...",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.3f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            if (hasCameraPermission) {
                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(640, 480))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            try {
                                cameraProvider.unbindAll()
                                cameraProvider.bindToLifecycle(
                                    lifecycleOwner,
                                    CameraSelector.DEFAULT_BACK_CAMERA,
                                    preview,
                                    imageAnalysis
                                )
                            } catch (_: Exception) {}
                        }, ContextCompat.getMainExecutor(ctx))
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            null,
                            tint = DarkOnSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "CAMERA_PERMISSION_REQUIRED",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                            Text("GRANT_ACCESS", color = SynthCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cornerLen = 40f
                val margin = w * 0.15f
                val viewfinderLeft = margin
                val viewfinderTop = h * 0.12f
                val viewfinderRight = w - margin
                val viewfinderBottom = h * 0.82f

                drawRect(
                    color = Color.Black.copy(alpha = 0.4f),
                    topLeft = Offset(0f, 0f),
                    size = GeoSize(w, h)
                )

                drawRect(
                    color = Color.Transparent,
                    topLeft = Offset(viewfinderLeft, viewfinderTop),
                    size = GeoSize(viewfinderRight - viewfinderLeft, viewfinderBottom - viewfinderTop),
                    blendMode = BlendMode.Clear
                )

                val cornerColor = if (isTargetAcquired) SynthGreen else SynthCyan
                val cornerAlpha = cornerPulse

                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderLeft, viewfinderTop), Offset(viewfinderLeft + cornerLen, viewfinderTop), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderLeft, viewfinderTop), Offset(viewfinderLeft, viewfinderTop + cornerLen), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderRight, viewfinderTop), Offset(viewfinderRight - cornerLen, viewfinderTop), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderRight, viewfinderTop), Offset(viewfinderRight, viewfinderTop + cornerLen), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderLeft, viewfinderBottom), Offset(viewfinderLeft + cornerLen, viewfinderBottom), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderLeft, viewfinderBottom), Offset(viewfinderLeft, viewfinderBottom - cornerLen), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderRight, viewfinderBottom), Offset(viewfinderRight - cornerLen, viewfinderBottom), strokeWidth = 3f)
                drawLine(cornerColor.copy(alpha = cornerAlpha), Offset(viewfinderRight, viewfinderBottom), Offset(viewfinderRight, viewfinderBottom - cornerLen), strokeWidth = 3f)

                val scanY = viewfinderTop + scanLineY * (viewfinderBottom - viewfinderTop)
                val scanGradient = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        cornerColor.copy(alpha = 0.15f),
                        cornerColor.copy(alpha = 0.3f),
                        cornerColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    startX = viewfinderLeft,
                    endX = viewfinderRight
                )
                drawRect(brush = scanGradient, topLeft = Offset(viewfinderLeft, scanY - 1f), size = GeoSize(viewfinderRight - viewfinderLeft, 2f))

                for (i in 0..4) {
                    val tickX = viewfinderLeft + (viewfinderRight - viewfinderLeft) * i / 4
                    drawLine(DarkBorder.copy(alpha = 0.3f), Offset(tickX, viewfinderBottom + 8f), Offset(tickX, viewfinderBottom + 16f), strokeWidth = 1f)
                }

                drawLine(
                    DarkBorder.copy(alpha = 0.2f),
                    Offset(viewfinderLeft, viewfinderTop + (viewfinderBottom - viewfinderTop) * 0.5f),
                    Offset(viewfinderLeft + 20f, viewfinderTop + (viewfinderBottom - viewfinderTop) * 0.5f),
                    strokeWidth = 1f
                )
                drawLine(
                    DarkBorder.copy(alpha = 0.2f),
                    Offset(viewfinderRight - 20f, viewfinderTop + (viewfinderBottom - viewfinderTop) * 0.5f),
                    Offset(viewfinderRight, viewfinderTop + (viewfinderBottom - viewfinderTop) * 0.5f),
                    strokeWidth = 1f
                )
            }

            if (isTargetAcquired) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 40.dp)
                ) {
                    GlassPanel(alpha = 0.6f, cornerRadius = 8.dp, glowColor = SynthGreen, glowIntensity = 0.3f) {
                        Text(
                            "[ TARGET_ACQUIRED ]",
                            color = SynthGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            if (detected != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(20.dp)
                ) {
                    GlassPanel(alpha = 0.7f, cornerRadius = 10.dp, glowColor = SynthCyan, glowIntensity = 0.2f) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "MODEL_MATCH:",
                                color = DarkOnSurfaceVariant.copy(alpha = 0.6f),
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                "${modelMatch}%",
                                color = SynthCyan,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Light,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "${detected.brand} ${detected.name}",
                                color = DarkOnSurface,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            if (isRecording && analysis.amplitude > 0.02f) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                ) {
                    Canvas(modifier = Modifier.size(40.dp)) {
                        val cx = size.width / 2
                        val cy = size.height / 2
                        for (i in 0..3) {
                            val r = 6f + i * 5f
                            val a = (0.4f - i * 0.1f).coerceAtLeast(0.05f)
                            drawCircle(
                                SynthGreen.copy(alpha = a * analysis.amplitude * 3f),
                                r,
                                Offset(cx, cy)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (detected != null) {
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                alpha = 0.45f,
                cornerRadius = 12.dp,
                glowColor = SynthCyan,
                glowIntensity = 0.15f
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SynthCyan.copy(alpha = 0.1f))
                            .border(1.dp, SynthCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MusicNote, null, tint = SynthCyan, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "${detected.brand} ${detected.name}",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            "${detected.category} // ${detected.waveformType}",
                            color = SynthCyan.copy(alpha = 0.7f),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            "${modelMatch}%",
                            color = SynthGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Light,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            "MATCH",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            GlassPanel(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                alpha = 0.3f,
                cornerRadius = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Radar,
                        null,
                        tint = DarkOnSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "SCANNING_FOR_SYNTHESIZERS...",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.3f,
                cornerRadius = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.GraphicEq, null, tint = SynthCyan.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("OSC_COUNT", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            if (detected != null) detected.oscillators.take(20) else "---",
                            color = SynthCyan.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
            GlassPanel(
                modifier = Modifier.weight(1f),
                alpha = 0.3f,
                cornerRadius = 10.dp
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(Icons.Default.Tune, null, tint = SynthMagenta.copy(alpha = 0.6f), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text("FILTER_TYPE", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 7.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            if (detected != null) detected.filterType.take(20) else "---",
                            color = SynthMagenta.copy(alpha = 0.8f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}
