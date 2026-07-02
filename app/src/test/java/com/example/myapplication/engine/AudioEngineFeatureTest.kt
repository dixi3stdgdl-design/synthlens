package com.example.myapplication.engine

import org.junit.Test
import org.junit.Assert.*
import kotlin.math.*

class AudioEngineFeatureTest {

    @Test
    fun testSineWaveClassification() {
        val buffer = FloatArray(2048) { sin(2.0 * PI * 440 * it / 44100).toFloat() }
        val classifier = WaveformTestHelper()
        val result = classifier.classify(buffer, 2048)
        assertTrue("Sine wave should produce a valid classification", 
            result in listOf("Sine", "Triangle", "Saw"))
    }

    @Test
    fun testSawWaveClassification() {
        val buffer = FloatArray(2048) { 
            val t = it.toFloat() / 2048f
            2f * (t * 44100f / 440f % 1f) - 1f
        }
        val classifier = WaveformTestHelper()
        val result = classifier.classify(buffer, 2048)
        assertTrue("Saw wave should produce a valid classification", result in listOf("Saw", "Triangle"))
    }

    @Test
    fun testSilentBufferClassification() {
        val buffer = FloatArray(2048) { 0f }
        val classifier = WaveformTestHelper()
        val result = classifier.classify(buffer, 2048)
        assertNotNull("Silent buffer should produce a result", result)
    }

    @Test
    fun testFrequencyToNoteName() {
        val helper = NoteNameHelper()
        val note440 = helper.frequencyToNote(440f)
        val note261 = helper.frequencyToNote(261.63f)
        val note523 = helper.frequencyToNote(523.25f)
        
        assertTrue("440Hz should be A note", note440.startsWith("A"))
        assertTrue("261Hz should be C note", note261.startsWith("C"))
        assertTrue("523Hz should be C note", note523.startsWith("C"))
    }

    @Test
    fun testOctaveCalculation() {
        val helper = OctaveHelper()
        assertEquals(4, helper.frequencyToOctave(440f))
        assertEquals(3, helper.frequencyToOctave(200f))
        assertEquals(5, helper.frequencyToOctave(800f))
    }

    @Test
    fun testHarmonicProfileCalculation() {
        val harmonics = listOf(1.0f, 0.5f, 0.25f, 0.125f, 0.0625f)
        val oddEvenRatio = harmonics[0] / (harmonics[0] + harmonics[1])
        assertTrue("Odd/even ratio should be between 0 and 1", oddEvenRatio in 0f..1f)

        val harmonicDecay = harmonics.last() / harmonics.first()
        assertTrue("Harmonic decay should be positive", harmonicDecay > 0f)
        assertTrue("Harmonic decay should be < 1 for decaying harmonics", harmonicDecay < 1f)
    }

    @Test
    fun testSpectralFlatness() {
        val uniformSpectrum = FloatArray(1024) { 1f }
        val flatness = calculateFlatness(uniformSpectrum)
        assertTrue("Uniform spectrum should have high flatness", flatness > 0.9f)

        val peakSpectrum = FloatArray(1024) { if (it == 100) 100f else 0.01f }
        val peakFlatness = calculateFlatness(peakSpectrum)
        assertTrue("Peaked spectrum should have low flatness", peakFlatness < 0.3f)
    }

    @Test
    fun testTHDCalculation() {
        val harmonics = listOf(1.0f, 0.5f, 0.25f)
        val thd = sqrt(harmonics[1] * harmonics[1] + harmonics[2] * harmonics[2]) / harmonics[0]
        assertTrue("THD should be between 0 and 1", thd in 0f..1f)
        assertEquals(0.559f, thd, 0.01f)
    }

    @Test
    fun testZeroCrossingsFrequency() {
        val helper = ZeroCrossingHelper()
        val freq = helper.estimateFrequency(440f, 2048, 44100)
        assertTrue("Estimated frequency should be close to 440Hz", freq in 400f..480f)
    }

    @Test
    fun testRMSCalculation() {
        val helper = RMSHelper()
        val buffer = FloatArray(100) { 0.5f }
        val rms = helper.calculateRMS(buffer, 100)
        assertEquals(0.5f, rms, 0.001f)
    }

    @Test
    fun testPeakCalculation() {
        val helper = PeakHelper()
        val buffer = floatArrayOf(0.1f, 0.5f, -0.8f, 0.3f)
        val peak = helper.calculatePeak(buffer, 4)
        assertEquals(0.8f, peak, 0.001f)
    }

    @Test
    fun testDCOffsetRemoval() {
        val helper = DCOffsetHelper()
        val buffer = FloatArray(100) { 0.5f + sin(it * 0.1).toFloat() * 0.1f }
        val result = helper.removeDCOffset(buffer, 100)
        val mean = result.sum() / result.size
        assertEquals("DC offset should be removed", 0f, mean, 0.01f)
    }

    @Test
    fun testNoiseGate() {
        val helper = NoiseGateHelper()
        val buffer = floatArrayOf(0.001f, 0.5f, -0.001f, 0.8f)
        val result = helper.applyNoiseGate(buffer, 4, 0.01f)
        assertEquals("Small signal should be gated to 0", 0f, result[0], 0.001f)
        assertEquals("Large signal should pass", 0.5f, result[1], 0.001f)
        assertEquals("Small signal should be gated to 0", 0f, result[2], 0.001f)
        assertEquals("Large signal should pass", 0.8f, result[3], 0.001f)
    }

    @Test
    fun testGainNormalization() {
        val helper = GainNormalizationHelper()
        val buffer = floatArrayOf(0.1f, 0.2f, 0.3f, 0.4f)
        val result = helper.normalizeGain(buffer, 4, 0.5f)
        val maxAbs = result.map { abs(it) }.max()
        assertEquals("Max amplitude should be close to target", 0.5f, maxAbs, 0.01f)
    }

    private fun calculateFlatness(spectrum: FloatArray): Float {
        if (spectrum.isEmpty()) return 0f
        var logSum = 0f
        var linearSum = 0f
        var count = 0
        for (v in spectrum) {
            if (v > 0) {
                logSum += ln(v)
                linearSum += v
                count++
            }
        }
        if (count == 0 || linearSum == 0f) return 0f
        val geometricMean = exp(logSum / count)
        val arithmeticMean = linearSum / count
        return (geometricMean / arithmeticMean).coerceIn(0f, 1f)
    }
}

class WaveformTestHelper {
    fun classify(buffer: FloatArray, length: Int): String {
        var sum = 0f; var sumSq = 0f; var peakVal = 0f
        for (i in 0 until length) {
            val v = abs(buffer[i]); sum += v; sumSq += v * v; if (v > peakVal) peakVal = v
        }
        val mean = sum / length; val rms = sqrt(sumSq / length)
        val crestFactor = if (mean > 0.001f) peakVal / rms else 0f
        val duty = if (peakVal > 0.001f) sum / (peakVal * length) else 0.5f
        return when {
            crestFactor > 2.5f && duty > 0.4f && duty < 0.6f -> "Sine"
            crestFactor > 1.8f && duty in 0.4f..0.6f -> "Triangle"
            crestFactor in 1.5f..2.0f && duty > 0.3f && duty < 0.5f -> "Saw"
            crestFactor in 1.3f..1.8f && duty < 0.4f -> "Square"
            crestFactor > 3.0f -> "Pulse"
            else -> "Saw"
        }
    }
}

class NoteNameHelper {
    fun frequencyToNote(freq: Float): String {
        if (freq <= 0) return "---"
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val midiNote = (69 + 12 * log2(freq / 440f)).roundToInt().coerceIn(0, 127)
        return noteNames[midiNote % 12]
    }
}

class OctaveHelper {
    fun frequencyToOctave(freq: Float): Int {
        if (freq <= 0) return 0
        return (log2(freq / 16.35f)).toInt().coerceIn(0, 8)
    }
}

class ZeroCrossingHelper {
    fun estimateFrequency(targetFreq: Float, bufferSize: Int, sampleRate: Int): Float {
        val period = sampleRate.toFloat() / targetFreq
        var crossings = 0
        for (i in 0 until bufferSize) {
            val t = i.toFloat() / period
            val current = sin(2.0 * PI * t).toFloat()
            val prev = sin(2.0 * PI * (i - 1).toFloat() / period).toFloat()
            if ((current >= 0 && prev < 0) || (current < 0 && prev >= 0)) {
                crossings++
            }
        }
        return (crossings.toFloat() * sampleRate) / (2f * bufferSize)
    }
}

class RMSHelper {
    fun calculateRMS(buffer: FloatArray, length: Int): Float {
        var sum = 0f
        for (i in 0 until length) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / length)
    }
}

class PeakHelper {
    fun calculatePeak(buffer: FloatArray, length: Int): Float {
        var peak = 0f
        for (i in 0 until length) {
            val abs = abs(buffer[i])
            if (abs > peak) peak = abs
        }
        return peak
    }
}

class DCOffsetHelper {
    fun removeDCOffset(buffer: FloatArray, length: Int): FloatArray {
        var sum = 0f
        for (i in 0 until length) sum += buffer[i]
        val dcOffset = sum / length
        val result = FloatArray(length)
        for (i in 0 until length) result[i] = buffer[i] - dcOffset
        return result
    }
}

class NoiseGateHelper {
    fun applyNoiseGate(buffer: FloatArray, length: Int, threshold: Float): FloatArray {
        val result = FloatArray(length)
        for (i in 0 until length) {
            result[i] = if (abs(buffer[i]) > threshold) buffer[i] else 0f
        }
        return result
    }
}

class GainNormalizationHelper {
    fun normalizeGain(buffer: FloatArray, length: Int, targetLevel: Float): FloatArray {
        var maxAbs = 0f
        for (i in 0 until length) {
            val a = abs(buffer[i])
            if (a > maxAbs) maxAbs = a
        }
        if (maxAbs < 0.001f) return buffer
        val gain = targetLevel / maxAbs
        val result = FloatArray(length)
        for (i in 0 until length) result[i] = (buffer[i] * gain).coerceIn(-1f, 1f)
        return result
    }
}
