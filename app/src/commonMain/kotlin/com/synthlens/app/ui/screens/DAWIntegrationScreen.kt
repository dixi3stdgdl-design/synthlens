package com.synthlens.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.synthlens.app.engine.AudioEngine

@Composable
fun DAWIntegrationScreen(audioEngine: AudioEngine) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("DAWIntegrationScreen is currently platform-specific and being migrated.", color = MaterialTheme.colorScheme.onBackground)
    }
}
