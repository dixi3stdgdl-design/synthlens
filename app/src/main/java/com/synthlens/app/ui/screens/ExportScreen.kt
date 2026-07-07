package com.synthlens.app.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synthlens.app.data.DetectionHistory
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ExportScreen(viewModel: SynthViewModel = viewModel()) {
    val context = LocalContext.current
    val history by viewModel.history.collectAsState()
    var selectedEntry by remember { mutableStateOf<DetectionHistory?>(null) }

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
            Text(
                "EXPORT_SHARE",
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
            Text(
                "SOCIAL_MEDIA",
                color = SynthGreen,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.35f,
            cornerRadius = 12.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("SELECT_DETECTION", color = SynthAmber, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(8.dp))

                if (history.isEmpty()) {
                    Text("NO_DETECTIONS_TO_EXPORT", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                } else {
                    history.take(10).forEach { entry ->
                        val isSelected = selectedEntry?.id == entry.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) SynthCyan.copy(alpha = 0.1f) else Color.Transparent)
                                .clickable { selectedEntry = entry }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isSelected) SynthCyan else DarkOnSurfaceVariant.copy(alpha = 0.3f))
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(entry.synthName, color = DarkOnSurface, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                Text("${entry.brand} // ${(entry.confidence * 100).toInt()}%", color = DarkOnSurfaceVariant.copy(alpha = 0.4f), fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }
                }
            }
        }

        if (selectedEntry != null) {
            Spacer(modifier = Modifier.height(16.dp))

            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                alpha = 0.4f,
                cornerRadius = 12.dp,
                glowColor = SynthCyan,
                glowIntensity = 0.15f
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("SHARE_TO", color = SynthGreen, fontSize = 10.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    val shareText = generateShareText(selectedEntry!!)
                    val dateStr = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(selectedEntry!!.detectedAt))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShareButton("WHATSAPP", SynthGreen, Modifier.weight(1f)) {
                            shareToApp(context, "com.whatsapp", shareText)
                        }
                        ShareButton("INSTAGRAM", SynthMagenta, Modifier.weight(1f)) {
                            shareToApp(context, "com.instagram.android", shareText)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ShareButton("TWITTER", SynthCyan, Modifier.weight(1f)) {
                            shareToApp(context, "com.twitter.android", shareText)
                        }
                        ShareButton("FACEBOOK", SynthPurple, Modifier.weight(1f)) {
                            shareToApp(context, "com.facebook.katana", shareText)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ShareButton("COPY_TEXT", SynthAmber, Modifier.fillMaxWidth()) {
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("SynthLens", shareText)
                        clipboard.setPrimaryClip(clip)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    GlassPanel(
                        modifier = Modifier.fillMaxWidth(),
                        alpha = 0.2f,
                        cornerRadius = 8.dp
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("PREVIEW:", color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(shareText, color = DarkOnSurface.copy(alpha = 0.7f), fontSize = 9.sp, fontFamily = FontFamily.Monospace, lineHeight = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun ShareButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    GlassPanel(
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() },
        alpha = 0.35f,
        cornerRadius = 10.dp,
        glowColor = color,
        glowIntensity = 0.15f
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                label,
                color = color,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }
    }
}

private fun generateShareText(entry: DetectionHistory): String {
    val dateStr = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault()).format(Date(entry.detectedAt))
    return buildString {
        appendLine("SYNTHLENS // Synth Detected")
        appendLine()
        appendLine("Synth: ${entry.synthName}")
        appendLine("Brand: ${entry.brand}")
        appendLine("Category: ${entry.category}")
        appendLine("Confidence: ${(entry.confidence * 100).toInt()}%")
        appendLine("Waveform: ${entry.waveformType}")
        appendLine("Frequency: ${entry.frequencyHz.toInt()}Hz")
        appendLine("Date: $dateStr")
        appendLine()
        appendLine("Detected with SynthLens v2.0")
        appendLine("#SynthLens #SynthDetection #MusicProduction")
    }
}

private fun shareToApp(context: android.content.Context, packageName: String, text: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
            setPackage(packageName)
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "Share via"))
    }
}
