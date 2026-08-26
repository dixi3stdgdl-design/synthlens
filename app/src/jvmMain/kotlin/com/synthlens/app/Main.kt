package com.synthlens.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.synthlens.app.engine.JvmAudioEngine
import com.synthlens.app.ui.theme.SynthLensTheme

fun main() = application {
    val audioEngine = JvmAudioEngine()
    
    Window(
        onCloseRequest = {
            audioEngine.destroy()
            exitApplication()
        },
        title = "SynthLens"
    ) {
        SynthLensTheme {
            SynthLensApp(audioEngine)
        }
    }
}
