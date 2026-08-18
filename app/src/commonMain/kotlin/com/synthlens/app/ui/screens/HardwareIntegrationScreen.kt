package com.synthlens.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun HardwareIntegrationScreen(onBack: () -> Unit = {}) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("HardwareIntegrationScreen is currently platform-specific and being migrated.", color = MaterialTheme.colorScheme.onBackground)
    }
}
