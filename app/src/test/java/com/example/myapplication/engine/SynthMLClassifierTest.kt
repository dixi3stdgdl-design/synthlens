package com.example.myapplication.engine

import org.junit.Test
import org.junit.Assert.*

class SynthMLClassifierTest {

    @Test
    fun testSynthFeaturesCreation() {
        val features = SynthFeatures(
            frequency = 440f,
            amplitude = 0.5f,
            spectralCentroid = 1000f,
            brightness = 0.3f,
            warmth = 0.7f,
            octave = 4
        )

        assertEquals(440f, features.frequency)
        assertEquals(0.5f, features.amplitude)
        assertEquals(0.3f, features.brightness)
        assertEquals(0.7f, features.warmth)
        assertEquals(4, features.octave)
    }

    @Test
    fun testAudioAnalysisToSynthFeatures() {
        val analysis = AudioAnalysis(
            frequency = 440f,
            amplitude = 0.5f,
            waveformType = "Saw",
            octaves = 4,
            harmonics = listOf(1.0f, 0.8f, 0.6f, 0.4f, 0.2f),
            spectralFlatness = 0.3f,
            spectralRolloff = 3000f,
            spectralBandwidth = 1500f,
            harmonicToNoiseRatio = 20f,
            thd = 0.5f
        )

        val features = analysis.toSynthFeatures()

        assertEquals(440f, features.frequency)
        assertEquals(0.5f, features.amplitude)
        assertEquals(1f, features.waveformSaw)
        assertEquals(0f, features.waveformSine)
        assertEquals(0f, features.waveformSquare)
        assertTrue(features.brightness >= 0f)
        assertTrue(features.warmth >= 0f)
        assertEquals(4, features.octave)
    }

    @Test
    fun testWaveformFeatureFlags() {
        val sawAnalysis = AudioAnalysis(waveformType = "Saw")
        assertEquals(1f, sawAnalysis.toSynthFeatures().waveformSaw)

        val sineAnalysis = AudioAnalysis(waveformType = "Sine")
        assertEquals(1f, sineAnalysis.toSynthFeatures().waveformSine)

        val squareAnalysis = AudioAnalysis(waveformType = "Square")
        assertEquals(1f, squareAnalysis.toSynthFeatures().waveformSquare)

        val triangleAnalysis = AudioAnalysis(waveformType = "Triangle")
        assertEquals(1f, triangleAnalysis.toSynthFeatures().waveformTriangle)

        val pulseAnalysis = AudioAnalysis(waveformType = "Pulse")
        assertEquals(1f, pulseAnalysis.toSynthFeatures().waveformPulse)
    }

    @Test
    fun testHarmonicProfileFromAnalysis() {
        val analysis = AudioAnalysis(
            harmonics = listOf(1.0f, 0.5f, 0.3f, 0.2f, 0.1f, 0.05f)
        )
        val features = analysis.toSynthFeatures()

        assertTrue("oddEvenRatio should be 0-1", features.oddEvenRatio in 0f..1f)
        assertTrue("harmonicDecay should be 0-1", features.harmonicDecay in 0f..1f)
        assertTrue("brightness should be 0-1", features.brightness in 0f..1f)
        assertTrue("warmth should be 0-1", features.warmth in 0f..1f)
    }

    @Test
    fun testMLClassificationResult() {
        val result = MLClassificationResult(
            synthName = "Moog Grandmother",
            brand = "Moog",
            category = "Semi-Modular Analog",
            confidence = 0.85f,
            allScores = mapOf("moog_grandmother" to 0.85f, "moog_sub37" to 0.6f)
        )

        assertEquals("Moog Grandmother", result.synthName)
        assertEquals("Moog", result.brand)
        assertEquals(0.85f, result.confidence)
        assertEquals(2, result.allScores.size)
    }
}
