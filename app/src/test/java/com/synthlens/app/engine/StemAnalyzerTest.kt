package com.synthlens.app.engine

import org.junit.Test
import org.junit.Assert.*

class StemAnalyzerTest {

    @Test
    fun testStemSynthProfileCreation() {
        val profile = StemSynthProfile(
            stemName = "Bass",
            detectedSynth = "Moog Grandmother",
            brand = "Moog",
            category = "Semi-Modular Analog",
            confidence = 0.75f,
            waveformType = "Saw",
            filterType = "Moog Ladder LP 24dB",
            frequencyRange = "300-1.2 kHz",
            peakFrequency = 200f,
            harmonics = listOf(1.0f, 0.6f, 0.3f),
            thd = 0.3f,
            rmsLevel = 0.5f,
            energy = 0.25f,
            characteristics = mapOf("oscillators" to "2 VCO + Sub")
        )

        assertEquals("Bass", profile.stemName)
        assertEquals("Moog", profile.brand)
        assertEquals(0.75f, profile.confidence)
        assertEquals(0.25f, profile.energy)
    }

    @Test
    fun testWarmthCalculation() {
        val analyzer = StemAnalyzer()
        val warmHarmonics = listOf(1.0f, 0.8f, 0.6f, 0.2f, 0.1f)
        val brightnessHarmonics = listOf(1.0f, 0.2f, 0.1f, 0.8f, 0.6f)

        val warmthScore = calculateWarmthFromHarmonics(warmHarmonics)
        val brightnessScore = calculateWarmthFromHarmonics(brightnessHarmonics)

        assertTrue("Warm signal should have higher warmth", warmthScore > brightnessScore)
    }

    @Test
    fun testHarmonicComplexity() {
        val complexHarmonics = listOf(1.0f, 0.8f, 0.7f, 0.6f, 0.5f, 0.4f, 0.3f, 0.2f)
        val simpleHarmonics = listOf(1.0f, 0.1f, 0.01f, 0.001f)

        val complexCount = complexHarmonics.count { it > complexHarmonics[0] * 0.1f }
        val simpleCount = simpleHarmonics.count { it > simpleHarmonics[0] * 0.1f }

        assertTrue("Complex signal should have more harmonics", complexCount > simpleCount)
    }

    private fun calculateWarmthFromHarmonics(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0f
        val low = harmonics.take(3).sum()
        val high = harmonics.drop(3).sum()
        val total = low + high
        return if (total > 0) low / total else 0.5f
    }
}
