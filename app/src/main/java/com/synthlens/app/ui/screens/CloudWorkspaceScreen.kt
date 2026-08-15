package com.synthlens.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
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
import com.synthlens.app.data.CloudWorkspaceRepository
import com.synthlens.app.ui.theme.DarkBackground
import com.synthlens.app.ui.theme.SynthCyan
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudWorkspaceScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { CloudWorkspaceRepository(context) }
    val patches by repo.syncWorkspace().collectAsState(initial = emptyList())

    Scaffold(
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = { 
                    Text("WORKSPACE SYNC", color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBackground)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Cloud, contentDescription = null, tint = SynthCyan, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Studio Team Workspace",
                color = Color.White,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Real-time patch synchronization across your team.",
                color = Color.White.copy(alpha = 0.6f),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (patches.isEmpty()) {
                CircularProgressIndicator(color = SynthCyan)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    items(patches) { patch ->
                        val df = SimpleDateFormat("MMM dd, HH:mm", Locale.US)
                        val date = df.format(Date(patch.timestamp))
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White.copy(alpha = 0.05f))
                                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(patch.synthName, color = SynthCyan, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("${(patch.confidence * 100).toInt()}%", color = Color.Green.copy(alpha = 0.8f), fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("By: ${patch.author}", color = Color.White.copy(alpha = 0.7f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                                Text(date, color = Color.White.copy(alpha = 0.5f), fontFamily = FontFamily.Monospace, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
