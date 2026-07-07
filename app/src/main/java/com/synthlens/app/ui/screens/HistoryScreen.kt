package com.synthlens.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synthlens.app.data.DetectionHistory
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(viewModel: SynthViewModel = viewModel()) {
    val history by viewModel.history.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    val filteredHistory = remember(history, searchQuery, showFavoritesOnly) {
        history.filter { entry ->
            val matchesSearch = searchQuery.isEmpty() ||
                    entry.synthName.contains(searchQuery, ignoreCase = true) ||
                    entry.brand.contains(searchQuery, ignoreCase = true)
            val matchesFav = !showFavoritesOnly || entry.isFavorite
            matchesSearch && matchesFav
        }
    }

    val totalDetections = history.size
    val uniqueSynths = history.map { it.synthName }.distinct().size
    val uniqueBrands = history.map { it.brand }.distinct().size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "DETECTION_LOG",
                color = SynthCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 3.sp
            )
            Text(
                "${totalDetections} SCANS",
                color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.4f,
            cornerRadius = 12.dp
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    GlassMeasurementItemHistory("SCANS", "$totalDetections", Modifier.weight(1f))
                    GlassMeasurementItemHistory("SYNTHS", "$uniqueSynths", Modifier.weight(1f))
                    GlassMeasurementItemHistory("BRANDS", "$uniqueBrands", Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GlassPanel(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clickable { showFavoritesOnly = false },
                alpha = if (!showFavoritesOnly) 0.45f else 0.2f,
                cornerRadius = 10.dp,
                glowColor = if (!showFavoritesOnly) SynthCyan else Color.Transparent,
                glowIntensity = if (!showFavoritesOnly) 0.2f else 0f
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "ALL",
                        color = if (!showFavoritesOnly) SynthCyan else DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (!showFavoritesOnly) FontWeight.Medium else FontWeight.Light
                    )
                }
            }
            GlassPanel(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .clickable { showFavoritesOnly = true },
                alpha = if (showFavoritesOnly) 0.45f else 0.2f,
                cornerRadius = 10.dp,
                glowColor = if (showFavoritesOnly) SynthAmber else Color.Transparent,
                glowIntensity = if (showFavoritesOnly) 0.2f else 0f
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "FAVORITES",
                        color = if (showFavoritesOnly) SynthAmber else DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (showFavoritesOnly) FontWeight.Medium else FontWeight.Light
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.History,
                        null,
                        tint = DarkOnSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        if (showFavoritesOnly) "NO_FAVORITES_YET" else "NO_DETECTIONS_YET",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        "Start scanning synths to build your log",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.3f),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(filteredHistory, key = { it.id }) { entry ->
                    HistoryEntryCard(
                        entry = entry,
                        onFavoriteToggle = { viewModel.toggleHistoryFavorite(entry.id, !entry.isFavorite) },
                        onDelete = { viewModel.deleteHistoryEntry(entry) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
private fun HistoryEntryCard(
    entry: DetectionHistory,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val stemColors = listOf(SynthPurple, SynthMagenta, SynthCyan, SynthGreen)
    val dateStr = remember(entry.detectedAt) {
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        sdf.format(Date(entry.detectedAt))
    }
    val confidenceColor = when {
        entry.confidence > 0.7f -> SynthGreen
        entry.confidence > 0.4f -> SynthAmber
        else -> SynthRed
    }

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        alpha = 0.35f,
        cornerRadius = 12.dp,
        glowColor = confidenceColor,
        glowIntensity = if (entry.confidence > 0.5f) 0.15f else 0f
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(confidenceColor.copy(alpha = 0.3f), DarkSurfaceVariant)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            tint = confidenceColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            entry.synthName,
                            color = DarkOnSurface,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            "${entry.brand} // ${entry.category}",
                            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${(entry.confidence * 100).toInt()}%",
                        color = confidenceColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        dateStr,
                        color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                GlassChip(modifier = Modifier.weight(1f)) {
                    Text(
                        entry.waveformType,
                        color = stemColors.getOrElse(0) { SynthCyan },
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                GlassChip(modifier = Modifier.weight(1f)) {
                    Text(
                        "${entry.frequencyHz.toInt()}Hz",
                        color = DarkOnSurface.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                GlassChip(modifier = Modifier.weight(1f)) {
                    Text(
                        "OCT ${entry.octave}",
                        color = DarkOnSurface.copy(alpha = 0.7f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(32.dp)) {
                    Icon(
                        if (entry.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        null,
                        tint = if (entry.isFavorite) SynthAmber else DarkOnSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        tint = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GlassMeasurementItemHistory(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.padding(4.dp)
    ) {
        Text(label, color = DarkOnSurfaceVariant.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = SynthCyan, fontSize = 16.sp, fontWeight = FontWeight.Light, fontFamily = FontFamily.Monospace)
    }
}
