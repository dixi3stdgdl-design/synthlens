package com.example.myapplication.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import kotlin.math.*

data class BrandVisual(
    val primaryColor: Color,
    val secondaryColor: Color,
    val pattern: BrandPattern
)

enum class BrandPattern {
    KNOBS,      // Moog, Sequential - rotary knobs
    KEYS,       // Roland, Korg - keyboard silhouette
    MATRIX,     // Arturia MatrixBrute - grid matrix
    PATCH,      // Semi-modular - patch points
    DIGITAL,    // Digital synths - screen/waveform
    WAVEFORM    // Generic - animated waveform
}

fun getBrandVisual(brand: String, category: String = ""): BrandVisual {
    val lower = brand.lowercase()
    return when {
        lower.contains("moog") -> BrandVisual(
            SynthAmber, Color(0xFF8B6914),
            if (category.contains("Modular")) BrandPattern.PATCH else BrandPattern.KNOBS
        )
        lower.contains("korg") -> BrandVisual(
            SynthCyan, Color(0xFF006064),
            if (lower.contains("opsix") || lower.contains("wavestate")) BrandPattern.DIGITAL else BrandPattern.KEYS
        )
        lower.contains("roland") -> BrandVisual(
            Color(0xFFFF6F00), Color(0xFFBF360C),
            BrandPattern.KEYS
        )
        lower.contains("sequential") || lower.contains("oberheim") -> BrandVisual(
            Color(0xFFE040FB), Color(0xFF7B1FA2),
            BrandPattern.KNOBS
        )
        lower.contains("novation") -> BrandVisual(
            SynthGreen, Color(0xFF1B5E20),
            BrandPattern.DIGITAL
        )
        lower.contains("arturia") -> BrandVisual(
            Color(0xFFFF5252), Color(0xFFB71C1C),
            if (category.contains("Modular")) BrandPattern.MATRIX else BrandPattern.KNOBS
        )
        lower.contains("behringer") -> BrandVisual(
            Color(0xFF448AFF), Color(0xFF0D47A1),
            BrandPattern.KEYS
        )
        lower.contains("yamaha") -> BrandVisual(
            Color(0xFF7C4DFF), Color(0xFF311B92),
            BrandPattern.DIGITAL
        )
        lower.contains("elektron") -> BrandVisual(
            Color(0xFFFF6E40), Color(0xFFBF360C),
            BrandPattern.DIGITAL
        )
        lower.contains("waldorf") -> BrandVisual(
            Color(0xFF00BFA5), Color(0xFF004D40),
            BrandPattern.WAVEFORM
        )
        lower.contains("teenage") -> BrandVisual(
            Color(0xFFFFAB40), Color(0xFFE65100),
            BrandPattern.DIGITAL
        )
        else -> BrandVisual(SynthPurple, Color(0xFF4A148C), BrandPattern.WAVEFORM)
    }
}

@Composable
fun SynthVisualIcon(
    brand: String,
    category: String = "",
    name: String = "",
    modifier: Modifier = Modifier,
    size: Int = 56
) {
    val visual = remember(brand, category) { getBrandVisual(brand, category) }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF080810))
            .border(0.5.dp, visual.primaryColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
            val sz = this.size
            val cw = sz.width - 8.dp.toPx()
            val ch = sz.height - 8.dp.toPx()
            val ccx = cw / 2
            val ccy = ch / 2

            when (visual.pattern) {
                BrandPattern.KNOBS -> {
                    val knobR = minOf(cw, ch) * 0.2f
                    for (row in 0..1) {
                        for (col in 0..2) {
                            val kx = cw * 0.2f + col * (cw * 0.3f)
                            val ky = ch * 0.3f + row * (ch * 0.4f)
                            drawCircle(Color(0xFF1A1A28), knobR, Offset(kx, ky))
                            drawCircle(visual.primaryColor.copy(alpha = 0.15f), knobR, Offset(kx, ky))
                            drawCircle(visual.primaryColor.copy(alpha = 0.4f), knobR * 0.3f, Offset(kx, ky))
                            val indicatorAngle = (col * 45f + row * 90f) * PI.toFloat() / 180f
                            val ix = kx + cos(indicatorAngle) * knobR * 0.6f
                            val iy = ky + sin(indicatorAngle) * knobR * 0.6f
                            drawCircle(visual.primaryColor.copy(alpha = 0.6f), 2.dp.toPx(), Offset(ix, iy))
                        }
                    }
                }
                BrandPattern.KEYS -> {
                    val keyW = cw / 7f
                    val whiteKeyH = ch * 0.7f
                    val blackKeyH = ch * 0.45f
                    val blackKeyW = keyW * 0.6f
                    for (i in 0..6) {
                        val kx = i * keyW
                        drawRoundRect(
                            Color.White.copy(alpha = 0.08f),
                            Offset(kx + 1, ch - whiteKeyH),
                            Size(keyW - 2, whiteKeyH),
                            CornerRadius(2f)
                        )
                        drawRoundRect(
                            visual.primaryColor.copy(alpha = 0.12f),
                            Offset(kx + 1, ch - whiteKeyH),
                            Size(keyW - 2, whiteKeyH),
                            CornerRadius(2f)
                        )
                    }
                    for (i in listOf(0, 1, 3, 4, 5)) {
                        val kx = i * keyW + keyW * 0.65f
                        drawRoundRect(
                            Color(0xFF0A0A14),
                            Offset(kx, ch - blackKeyH),
                            Size(blackKeyW, blackKeyH),
                            CornerRadius(2f)
                        )
                        drawRoundRect(
                            visual.primaryColor.copy(alpha = 0.2f),
                            Offset(kx, ch - blackKeyH),
                            Size(blackKeyW, blackKeyH),
                            CornerRadius(2f)
                        )
                    }
                }
                BrandPattern.MATRIX -> {
                    val gridSize = 4
                    val cellSize = minOf(cw, ch) / (gridSize + 1)
                    for (row in 0 until gridSize) {
                        for (col in 0 until gridSize) {
                            val mx = (col + 0.5f) * cellSize
                            val my = (row + 0.5f) * cellSize
                            val isActive = (row + col) % 3 != 0
                            drawRoundRect(
                                if (isActive) visual.primaryColor.copy(alpha = 0.3f)
                                else Color.White.copy(alpha = 0.04f),
                                Offset(mx - cellSize * 0.3f, my - cellSize * 0.3f),
                                Size(cellSize * 0.6f, cellSize * 0.6f),
                                CornerRadius(2f)
                            )
                        }
                    }
                }
                BrandPattern.PATCH -> {
                    val points = listOf(
                        Offset(cw * 0.2f, ch * 0.2f),
                        Offset(cw * 0.5f, ch * 0.15f),
                        Offset(cw * 0.8f, ch * 0.25f),
                        Offset(cw * 0.15f, ch * 0.6f),
                        Offset(cw * 0.5f, ch * 0.55f),
                        Offset(cw * 0.85f, ch * 0.65f),
                        Offset(cw * 0.3f, ch * 0.85f),
                        Offset(cw * 0.7f, ch * 0.9f)
                    )
                    for (p in points) {
                        drawCircle(visual.primaryColor.copy(alpha = 0.5f), 3.dp.toPx(), p)
                        drawCircle(visual.primaryColor.copy(alpha = 0.15f), 6.dp.toPx(), p)
                    }
                    for (i in 0 until points.size - 1 step 2) {
                        drawLine(
                            visual.primaryColor.copy(alpha = 0.2f),
                            points[i], points[i + 1],
                            1.dp.toPx()
                        )
                    }
                }
                BrandPattern.DIGITAL -> {
                    drawRoundRect(
                        Color(0xFF0A0A14),
                        Offset(cw * 0.1f, ch * 0.15f),
                        Size(cw * 0.8f, ch * 0.5f),
                        CornerRadius(4f)
                    )
                    drawRoundRect(
                        visual.primaryColor.copy(alpha = 0.1f),
                        Offset(cw * 0.1f, ch * 0.15f),
                        Size(cw * 0.8f, ch * 0.5f),
                        CornerRadius(4f)
                    )
                    val path = Path()
                    for (i in 0..30) {
                        val x = cw * 0.15f + (i.toFloat() / 30f) * cw * 0.7f
                        val y = ch * 0.4f - sin(i * 0.5f) * ch * 0.1f
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, visual.primaryColor.copy(alpha = 0.5f), style = Stroke(1.5f))
                    for (i in 0..2) {
                        val bx = cw * 0.15f + i * cw * 0.25f
                        val by = ch * 0.75f
                        drawRoundRect(
                            visual.secondaryColor.copy(alpha = 0.3f),
                            Offset(bx, by),
                            Size(cw * 0.15f, ch * 0.12f),
                            CornerRadius(2f)
                        )
                    }
                }
                BrandPattern.WAVEFORM -> {
                    val path = Path()
                    for (i in 0..50) {
                        val x = (i.toFloat() / 50f) * cw
                        val y = ccy + sin(i * 0.3f) * ch * 0.25f * cos(i * 0.1f)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                    drawPath(path, visual.primaryColor.copy(alpha = 0.08f), style = Stroke(6f))
                    drawPath(path, visual.primaryColor.copy(alpha = 0.25f), style = Stroke(3f))
                    drawPath(path, visual.primaryColor.copy(alpha = 0.6f), style = Stroke(1.5f))
                }
            }
        }

        Text(
            name.take(2).uppercase(),
            color = visual.primaryColor.copy(alpha = 0.7f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(2.dp)
        )
    }
}
