package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.synthlens.app.ui.components.GlassPanel
import com.synthlens.app.ui.theme.*

@Composable
fun SubscriptionScreen(
    onBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = SynthCyan)
            }
            Text(
                text = "UPGRADE_SYNTHLENS",
                color = SynthCyan,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Unlock the ultimate AI Synthesizer Analyzer capabilities.",
                color = DarkOnSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // PRO TIER
            GlassPanel(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                alpha = 0.4f,
                cornerRadius = 16.dp,
                glowColor = SynthCyan,
                glowIntensity = 0.2f
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = "Pro", tint = SynthCyan, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SYNTHLENS PRO", color = SynthCyan, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Text("$4.99 / month", color = DarkOnSurface, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(vertical = 8.dp))
                    
                    HorizontalDivider(color = DarkBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    
                    FeatureItem("Offline detection with local models")
                    FeatureItem("Advanced Spectral Analysis metrics")
                    FeatureItem("Unlimited DAW exports")
                    FeatureItem("Extended synthesis historical logs")

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthCyan, contentColor = DarkBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("GO PRO", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // ENTERPRISE TIER
            GlassPanel(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                alpha = 0.4f,
                cornerRadius = 16.dp,
                glowColor = SynthAmber,
                glowIntensity = 0.3f
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = "Enterprise", tint = SynthAmber, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ENTERPRISE", color = SynthAmber, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Text("$99.00 / year", color = DarkOnSurface, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(vertical = 8.dp))
                    
                    HorizontalDivider(color = DarkBorder, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                    
                    FeatureItem("Everything in Pro")
                    FeatureItem("Batch processing multiple audio files")
                    FeatureItem("Custom synth tagging & team sharing")
                    FeatureItem("API Access to detection models")
                    FeatureItem("Priority tech support")

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { /* TODO */ },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SynthAmber, contentColor = DarkBackground),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("UPGRADE TO ENTERPRISE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FeatureItem(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Default.Check, contentDescription = null, tint = SynthGreen, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = DarkOnSurface, fontSize = 14.sp)
    }
}
