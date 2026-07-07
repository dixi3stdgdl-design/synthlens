package com.synthlens.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synthlens.app.data.SynthLibraryItem
import com.synthlens.app.ui.components.*
import com.synthlens.app.ui.theme.*
import com.synthlens.app.viewmodel.SynthViewModel
import kotlin.math.*

@Composable
fun LibraryScreen(
    viewModel: SynthViewModel = viewModel(),
    onSynthSelected: (SynthLibraryItem) -> Unit = {}
) {
    val allSynths by viewModel.allSynths.collectAsState()
    val filteredSynths by viewModel.filteredSynths.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedBrand by viewModel.selectedBrand.collectAsState()

    val brands = viewModel.getBrands()

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "OSC_SCANNER_V1",
                    color = DarkOnSurface,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "● SYS_READY",
                    color = SynthGreen.copy(alpha = 0.6f),
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "SYSTEM_PATCH_BAY",
            color = SynthAmber,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 2.sp
        )
        Text(
            "${allSynths.size} HARDWARE_UNITS DETECTED IN RACK",
            color = DarkOnSurfaceVariant.copy(alpha = 0.5f),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        GlassPanel(
            modifier = Modifier.fillMaxWidth(),
            alpha = 0.35f,
            cornerRadius = 10.dp
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "FILTER_HARDBARE...",
                        color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light,
                        fontFamily = FontFamily.Monospace
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, null, tint = SynthCyan.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = SynthCyan,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("ALL" to "ALL", "ANALOG" to "ANALOG", "DIGITAL" to "DIGITAL", "MODULAR" to "MODULAR").forEach { (id, label) ->
                val isSelected = selectedFilter == id
                GlassPanel(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp),
                    alpha = if (isSelected) 0.45f else 0.2f,
                    cornerRadius = 8.dp,
                    glowColor = if (isSelected) SynthCyan else Color.Transparent,
                    glowIntensity = if (isSelected) 0.2f else 0f
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(if (isSelected) SynthCyan.copy(alpha = 0.08f) else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (isSelected) SynthCyan else DarkOnSurfaceVariant.copy(alpha = 0.4f),
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Brand filter chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                BrandChip(
                    label = "ALL_BRANDS",
                    isSelected = selectedBrand == null,
                    onClick = { viewModel.updateBrand(null) }
                )
            }
            items(brands) { brand ->
                BrandChip(
                    label = brand.uppercase(),
                    isSelected = selectedBrand == brand,
                    onClick = { viewModel.updateBrand(brand) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Results count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "RESULTS: ${filteredSynths.size}",
                color = SynthGreen.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            if (selectedBrand != null) {
                Text(
                    "BRAND: $selectedBrand",
                    color = SynthAmber.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(filteredSynths) { synth ->
                SystemPatchCard(synth = synth, onClick = { onSynthSelected(synth) })
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun BrandChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    GlassPanel(
        modifier = Modifier
            .height(32.dp)
            .clickable { onClick() },
        alpha = if (isSelected) 0.45f else 0.2f,
        cornerRadius = 8.dp,
        glowColor = if (isSelected) SynthAmber else Color.Transparent,
        glowIntensity = if (isSelected) 0.2f else 0f
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isSelected) SynthAmber.copy(alpha = 0.08f) else Color.Transparent)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                label,
                color = if (isSelected) SynthAmber else DarkOnSurfaceVariant.copy(alpha = 0.4f),
                fontSize = 8.sp,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Light,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun SystemPatchCard(synth: SynthLibraryItem, onClick: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing)
        )
    )
    val isBright = LocalIsBright.current

    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        alpha = if (synth.isDetected) 0.5f else 0.35f,
        cornerRadius = 12.dp,
        glowColor = if (synth.isDetected) SynthCyan else Color.White,
        glowIntensity = if (synth.isDetected) 0.15f else 0f
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                SynthVisualIcon(
                    brand = synth.brand,
                    category = synth.category,
                    name = synth.name,
                    size = 56
                )

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (synth.isDetected) SynthGreen else SynthAmber.copy(alpha = 0.5f))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            synth.name.uppercase(),
                            color = if (isBright) Color.White else DarkOnSurface,
                            fontSize = if (isBright) 14.sp else 13.sp,
                            fontWeight = if (isBright) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        if (synth.isDetected) {
                            Spacer(modifier = Modifier.width(6.dp))
                            GlassPanel(alpha = 0.3f, cornerRadius = 4.dp) {
                                Text(
                                    "DETECTED",
                                    color = SynthGreen,
                                    fontSize = 6.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        synth.brand.uppercase(),
                        color = SynthAmber.copy(alpha = if (isBright) 0.9f else 0.6f),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    if (synth.category.isNotEmpty()) {
                        Text(
                            synth.category.uppercase().take(25),
                            color = DarkOnSurfaceVariant.copy(alpha = if (isBright) 0.7f else 0.4f),
                            fontSize = 7.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SpecTag("OSC_TYPE", synth.waveformTypes.split(",").firstOrNull()?.trim()?.uppercase()?.take(12) ?: "SAW", SynthCyan, Modifier.weight(1f))
                SpecTag("CIRCUIT", synth.filterTypes.split(",").firstOrNull()?.trim()?.uppercase()?.take(15) ?: "LPF", SynthMagenta, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(10.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DarkSurfaceVariant)
            ) {
                val w = size.width
                val h = size.height
                val cy = h / 2
                val path = Path()

                for (i in 0..100) {
                    val x = (i.toFloat() / 100f) * w
                    val y = cy - sin(i * 0.15f + wavePhase) * (h * 0.3f) *
                            (0.3f + synth.waveformTypes.lowercase().let { wf ->
                                when {
                                    wf.contains("saw") -> 0.8f
                                    wf.contains("square") -> 0.9f
                                    wf.contains("pulse") -> 0.7f
                                    else -> 0.5f
                                }
                            })
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }

                drawPath(path, SynthCyan.copy(alpha = if (isBright) 0.2f else 0.08f), style = Stroke(width = 6f))
                drawPath(path, SynthCyan.copy(alpha = if (isBright) 0.5f else 0.25f), style = Stroke(width = 3f))
                drawPath(path, SynthCyan.copy(alpha = if (isBright) 0.9f else 0.6f), style = Stroke(width = 1.2f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (synth.priceRange.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("$ ", color = SynthGreen.copy(alpha = if (isBright) 0.9f else 0.6f), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(
                            synth.priceRange.take(25),
                            color = SynthGreen.copy(alpha = if (isBright) 1f else 0.7f),
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                if (synth.bestFor.isNotEmpty()) {
                    Text(
                        synth.bestFor.take(30),
                        color = DarkOnSurfaceVariant.copy(alpha = if (isBright) 0.7f else 0.35f),
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecTag(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassPanel(
        modifier = modifier,
        alpha = 0.3f,
        cornerRadius = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$label: ",
                color = DarkOnSurfaceVariant.copy(alpha = 0.4f),
                fontSize = 7.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                value,
                color = color.copy(alpha = 0.8f),
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Light
            )
        }
    }
}
