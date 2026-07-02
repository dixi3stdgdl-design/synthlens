package com.example.myapplication.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import com.example.myapplication.ui.components.rememberAmbientBrightness
import com.example.myapplication.ui.components.isOutdoorBrightness

val LocalIsBright = compositionLocalOf { false }

private val SynthLensDarkScheme = darkColorScheme(
    primary = SynthCyan,
    onPrimary = DarkBackground,
    primaryContainer = SynthCyanDark,
    secondary = SynthMagenta,
    onSecondary = DarkBackground,
    secondaryContainer = SynthMagentaDark,
    tertiary = SynthPurple,
    tertiaryContainer = SynthPurpleDark,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onBackground = DarkOnSurface,
    onSurface = DarkOnSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkBorder,
    error = SynthRed,
    onError = Color.White
)

@Composable
fun SynthLensTheme(
    content: @Composable () -> Unit
) {
    val lux = rememberAmbientBrightness()
    val isBright = isOutdoorBrightness(lux)

    CompositionLocalProvider(LocalIsBright provides isBright) {
        MaterialTheme(
            colorScheme = SynthLensDarkScheme,
            typography = Typography,
            content = content
        )
    }
}
