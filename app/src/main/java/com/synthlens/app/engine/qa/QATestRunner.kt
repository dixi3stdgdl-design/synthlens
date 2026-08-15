package com.synthlens.app.engine.qa

import com.synthlens.app.engine.SynthFeatures
import kotlin.math.abs

data class QAResult(
    val pass: Boolean,
    val failedMetrics: List<String>,
    val toleranceUsed: Float
)

class QATestRunner {
    fun evaluateTolerance(features: SynthFeatures, targetFreq: Float, allowedCentsDrift: Float = 3.0f): QAResult {
        val diffHz = abs(features.frequency - targetFreq)
        
        // Approximate cents conversion for testing (1200 * log2(f1/f2))
        // Since this is a mockup for QA limits, we'll assume a basic frequency drift calculation
        val centDrift = 1200.0 * (kotlin.math.log2(features.frequency.toDouble() / targetFreq.toDouble()))
        val absDrift = abs(centDrift).toFloat()

        val failedMetrics = mutableListOf<String>()
        
        if (absDrift > allowedCentsDrift) {
            failedMetrics.add("Frequency Drift ($absDrift cents > $allowedCentsDrift cents)")
        }
        
        if (features.amplitude < 0.05f) {
            failedMetrics.add("Output Level too low (Amplitude = ${features.amplitude})")
        }

        return QAResult(
            pass = failedMetrics.isEmpty(),
            failedMetrics = failedMetrics,
            toleranceUsed = allowedCentsDrift
        )
    }
}
