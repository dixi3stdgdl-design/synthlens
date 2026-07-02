package com.example.myapplication.engine

import kotlin.math.*

data class Stem(
    val name: String,
    val frequencyRange: String,
    val samples: FloatArray,
    val spectrum: FloatArray,
    val rmsLevel: Float,
    val peakFrequency: Float,
    val harmonicProfile: List<Float>,
    val waveformType: String,
    val energy: Float
)

data class StemAnalysis(
    val stems: List<Stem>,
    val dominantStem: Stem?,
    val mixtureSpectrum: FloatArray,
    val separationConfidence: Float
)

class StemSeparator {

    companion object {
        const val STFTWindowSize = 2048
        const val STFTHopSize = 512
        const val SAMPLE_RATE = 44100

        val SUB_BASS_RANGE = 20f..300f
        val BASS_RANGE = 300f..1200f
        val MID_RANGE = 1200f..6000f
        val HIGH_RANGE = 6000f..20000f

        val STEM_NAMES = listOf("Sub-Bass", "Bass", "Mids", "Highs")
        val STEM_RANGES = listOf("20-300 Hz", "300-1.2 kHz", "1.2-6 kHz", "6-20 kHz")
    }

    private var stftBuffer = Array(STFTWindowSize) { FloatArray(2) }
    private var stftFrameIndex = 0
    private val spectralHistory = mutableListOf<FloatArray>()

    fun processBuffer(buffer: ShortArray, length: Int): StemAnalysis {
        val floatBuffer = FloatArray(length) { buffer[it].toFloat() / Short.MAX_VALUE }
        return processFloatBuffer(floatBuffer, length)
    }

    fun processFloatBuffer(buffer: FloatArray, length: Int): StemAnalysis {
        val windowed = applyHammingWindow(buffer, length)
        val stftFrame = computeSTFT(windowed, length)

        if (spectralHistory.size > 60) {
            spectralHistory.removeAt(0)
        }
        spectralHistory.add(stftFrame.copyOf())

        val mixtureSpectrum = computeAverageSpectrum()

        val stems = separateStems(stftFrame, windowed, length)
        val dominant = stems.maxByOrNull { it.energy }

        val totalEnergy = stems.sumOf { it.energy.toDouble() }.toFloat()
        val separationConfidence = if (totalEnergy > 0) {
            val energies = stems.map { (it.energy / totalEnergy).toDouble() }
            val entropy = -energies.sumOf { e ->
                if (e > 0.001) e * ln(e) else 0.0
            }.toFloat()
            val maxEntropy = ln(stems.size.toDouble()).toFloat()
            if (maxEntropy > 0) 1f - (entropy / maxEntropy) else 0.5f
        } else 0f

        return StemAnalysis(
            stems = stems,
            dominantStem = dominant,
            mixtureSpectrum = mixtureSpectrum,
            separationConfidence = separationConfidence.coerceIn(0f, 1f)
        )
    }

    private fun applyHammingWindow(buffer: FloatArray, length: Int): FloatArray {
        val windowed = FloatArray(STFTWindowSize)
        val n = minOf(length, STFTWindowSize)
        for (i in 0 until n) {
            val hamming = 0.54f - 0.46f * cos(2.0 * PI * i / (n - 1)).toFloat()
            windowed[i] = buffer[i] * hamming
        }
        return windowed
    }

    private fun computeSTFT(windowed: FloatArray, length: Int): FloatArray {
        val n = STFTWindowSize
        val real = FloatArray(n)
        val imag = FloatArray(n)

        for (i in 0 until minOf(length, n)) {
            real[i] = windowed[i]
        }

        fft(real, imag, n)

        val spectrumSize = n / 2
        val spectrum = FloatArray(spectrumSize)
        for (k in 0 until spectrumSize) {
            spectrum[k] = sqrt(real[k] * real[k] + imag[k] * imag[k]) / n
        }
        return spectrum
    }

    private fun fft(real: FloatArray, imag: FloatArray, n: Int) {
        var j = 0
        for (i in 0 until n - 1) {
            if (i < j) {
                var temp = real[i]; real[i] = real[j]; real[j] = temp
                temp = imag[i]; imag[i] = imag[j]; imag[j] = temp
            }
            var m = n shr 1
            while (m >= 1 && j >= m) { j -= m; m = m shr 1 }
            j += m
        }

        var step = 1
        while (step < n) {
            val halfStep = step
            step = step shl 1
            val angle = -PI.toFloat() / halfStep
            val wReal = cos(angle)
            val wImag = sin(angle)

            var i = 0
            while (i < n) {
                var curWReal = 1f
                var curWImag = 0f

                for (k in 0 until halfStep) {
                    val tReal = curWReal * real[i + k + halfStep] - curWImag * imag[i + k + halfStep]
                    val tImag = curWReal * imag[i + k + halfStep] + curWImag * real[i + k + halfStep]
                    real[i + k + halfStep] = real[i + k] - tReal
                    imag[i + k + halfStep] = imag[i + k] - tImag
                    real[i + k] = real[i + k] + tReal
                    imag[i + k] = imag[i + k] + tImag
                    val newWReal = curWReal * wReal - curWImag * wImag
                    curWImag = curWReal * wImag + curWImag * wReal
                    curWReal = newWReal
                }
                i += step
            }
        }
    }

    private fun computeAverageSpectrum(): FloatArray {
        if (spectralHistory.isEmpty()) return FloatArray(0)

        val size = spectralHistory[0].size
        val avg = FloatArray(size)

        for (frame in spectralHistory) {
            for (i in 0 until minOf(frame.size, size)) {
                avg[i] += frame[i]
            }
        }

        val count = spectralHistory.size.toFloat()
        for (i in avg.indices) {
            avg[i] /= count
        }

        return avg
    }

    private fun separateStems(
        spectrum: FloatArray,
        buffer: FloatArray,
        length: Int
    ): List<Stem> {
        val binWidth = SAMPLE_RATE.toFloat() / STFTWindowSize
        val stems = mutableListOf<Stem>()

        val ranges = listOf(SUB_BASS_RANGE, BASS_RANGE, MID_RANGE, HIGH_RANGE)

        for (i in ranges.indices) {
            val range = ranges[i]
            val stem = extractStem(spectrum, buffer, length, range, binWidth, STEM_NAMES[i], STEM_RANGES[i])
            stems.add(stem)
        }

        return applyNMFSeparation(stems, spectrum, binWidth)
    }

    private fun extractStem(
        spectrum: FloatArray,
        buffer: FloatArray,
        length: Int,
        frequencyRange: ClosedFloatingPointRange<Float>,
        binWidth: Float,
        name: String,
        rangeLabel: String
    ): Stem {
        val startBin = (frequencyRange.start / binWidth).toInt().coerceIn(0, spectrum.size - 1)
        val endBin = (frequencyRange.endInclusive / binWidth).toInt().coerceIn(0, spectrum.size - 1)

        val bandSpectrum = FloatArray(endBin - startBin + 1)
        var bandEnergy = 0f
        var maxVal = 0f
        var maxBin = 0

        for (i in startBin..endBin) {
            if (i < spectrum.size) {
                val idx = i - startBin
                bandSpectrum[idx] = spectrum[i]
                bandEnergy += spectrum[i] * spectrum[i]
                if (spectrum[i] > maxVal) {
                    maxVal = spectrum[i]
                    maxBin = i
                }
            }
        }

        val rms = sqrt(bandEnergy / bandSpectrum.size.coerceAtLeast(1))
        val peakFreq = (maxBin * binWidth).coerceIn(20f, 20000f)

        val harmonics = extractHarmonicsFromBand(spectrum, peakFreq, binWidth)
        val waveform = classifyWaveformFromSpectrum(bandSpectrum, harmonics)

        val filteredBuffer = try {
            bandpassFilter(buffer, length, frequencyRange, binWidth)
        } catch (_: Exception) { FloatArray(length) }
        val filteredRms = calculateRMS(filteredBuffer, length)

        return Stem(
            name = name,
            frequencyRange = rangeLabel,
            samples = filteredBuffer.copyOf(length),
            spectrum = bandSpectrum,
            rmsLevel = rms,
            peakFrequency = peakFreq,
            harmonicProfile = harmonics,
            waveformType = waveform,
            energy = rms * rms
        )
    }

    private fun extractHarmonicsFromBand(
        spectrum: FloatArray,
        fundamental: Float,
        binWidth: Float
    ): List<Float> {
        val harmonics = mutableListOf<Float>()
        if (fundamental <= 0 || spectrum.isEmpty()) return harmonics

        for (h in 1..8) {
            val targetFreq = h * fundamental
            val targetBin = (targetFreq / binWidth).toInt()
            val start = maxOf(0, targetBin - 3)
            val end = minOf(spectrum.size - 1, targetBin + 3)
            var maxVal = 0f
            for (i in start..end) {
                if (i < spectrum.size && spectrum[i] > maxVal) {
                    maxVal = spectrum[i]
                }
            }
            harmonics.add(maxVal)
        }

        return harmonics
    }

    private fun classifyWaveformFromSpectrum(bandSpectrum: FloatArray, harmonics: List<Float>): String {
        if (harmonics.size < 2 || harmonics[0] <= 0) return "Unknown"

        val fundamental = harmonics[0]
        val secondHarmonic = harmonics.getOrNull(1) ?: 0f
        val thirdHarmonic = harmonics.getOrNull(2) ?: 0f
        val fourthHarmonic = harmonics.getOrNull(3) ?: 0f

        val h2Ratio = if (fundamental > 0) secondHarmonic / fundamental else 0f
        val h3Ratio = if (fundamental > 0) thirdHarmonic / fundamental else 0f
        val h4Ratio = if (fundamental > 0) fourthHarmonic / fundamental else 0f

        val oddEvenRatio = if (h2Ratio + h3Ratio > 0) h3Ratio / (h2Ratio + h3Ratio) else 0.5f
        val harmonicDecay = if (fundamental > 0 && harmonics.size > 1) {
            harmonics.last() / fundamental
        } else 0.5f

        val totalHarmonics = harmonics.sum()
        val highHarmonicEnergy = harmonics.drop(4).sum()
        val brightness = if (totalHarmonics > 0) highHarmonicEnergy / totalHarmonics else 0f

        return when {
            h3Ratio < 0.1f && h4Ratio < 0.05f && harmonicDecay > 0.3f -> "Sine"
            oddEvenRatio > 0.7f && h2Ratio < 0.3f -> "Triangle"
            h2Ratio > 0.4f && h3Ratio > 0.2f && brightness > 0.15f -> "Saw"
            oddEvenRatio > 0.6f && h2Ratio > 0.2f -> "Square"
            brightness > 0.2f && harmonicDecay > 0.4f -> "Pulse"
            h2Ratio > 0.5f -> "Saw"
            oddEvenRatio > 0.5f -> "Triangle"
            else -> "Saw"
        }
    }

    private fun bandpassFilter(
        buffer: FloatArray,
        length: Int,
        range: ClosedFloatingPointRange<Float>,
        binWidth: Float
    ): FloatArray {
        val filtered = FloatArray(length)
        val spectrumSize = length / 2
        val inputSpectrum = FloatArray(spectrumSize * 2)

        for (i in 0 until length) {
            inputSpectrum[i] = buffer[i]
        }

        val outputSpectrum = FloatArray(spectrumSize * 2)
        for (k in 0 until spectrumSize) {
            val freq = k * binWidth
            val inBand = freq >= range.start && freq <= range.endInclusive

            if (inBand) {
                val fadeIn = ((freq - range.start) / (range.start * 0.1f + 1f)).coerceIn(0f, 1f)
                val fadeOut = ((range.endInclusive - freq) / (range.endInclusive * 0.1f + 1f)).coerceIn(0f, 1f)
                val gain = fadeIn * fadeOut

                outputSpectrum[k] = inputSpectrum[k] * gain
                outputSpectrum[k + spectrumSize] = inputSpectrum[k + spectrumSize] * gain
            }
        }

        for (i in 0 until length) {
            var real = 0f
            for (k in 0 until spectrumSize) {
                val angle = (2.0 * PI * k * i / length).toFloat()
                real += outputSpectrum[k] * cos(angle) - outputSpectrum[k + spectrumSize] * sin(angle)
            }
            filtered[i] = (real * 2f / length).coerceIn(-1f, 1f)
        }

        return filtered
    }

    private fun calculateRMS(buffer: FloatArray, length: Int): Float {
        var sum = 0f
        val n = minOf(length, buffer.size)
        for (i in 0 until n) {
            sum += buffer[i] * buffer[i]
        }
        return sqrt(sum / n.coerceAtLeast(1))
    }

    private fun applyNMFSeparation(
        stems: List<Stem>,
        spectrum: FloatArray,
        binWidth: Float
    ): List<Stem> {
        if (stems.all { it.energy < 0.0001f }) return stems

        val totalEnergy = stems.sumOf { it.energy.toDouble() }.toFloat()
        if (totalEnergy <= 0) return stems

        val energyWeights = stems.map { it.energy / totalEnergy }

        val dominantIdx = energyWeights.indices.maxByOrNull { energyWeights[it] } ?: 0
        val dominantWeight = energyWeights[dominantIdx]

        return stems.mapIndexed { index, stem ->
            val weight = energyWeights[index]
            val confidence = when {
                weight > 0.4f -> 0.9f
                weight > 0.25f -> 0.75f
                weight > 0.15f -> 0.6f
                weight > 0.08f -> 0.4f
                else -> 0.2f
            }

            val adjustedRms = stem.rmsLevel * weight * 3f
            val adjustedEnergy = stem.energy * weight * 3f

            stem.copy(
                rmsLevel = adjustedRms.coerceIn(0f, 1f),
                energy = adjustedEnergy
            )
        }
    }

    fun getStemRecommendation(stem: Stem): String? {
        if (stem.energy < 0.001f) return null
        if (stem.rmsLevel < 0.01f) return null

        val fundamental = stem.peakFrequency
        val waveform = stem.waveformType
        val harmonics = stem.harmonicProfile

        if (fundamental < 20f || fundamental > 20000f) return null

        val warmScore = calculateWarmthScore(harmonics)
        val brightScore = calculateBrightnessScore(harmonics)
        val harmonicComplexity = harmonics.count { it > harmonics.getOrElse(0) { 1f } * 0.1f }

        return when (stem.name) {
            "Sub-Bass" -> when {
                waveform == "Sine" && fundamental < 100f -> "Moog Grandmother / Sub 37"
                waveform == "Saw" && fundamental < 150f -> "Korg MS-20"
                waveform == "Square" -> "Roland TB-303"
                else -> "Generic Analog Bass"
            }
            "Bass" -> when {
                warmScore > 0.6f && waveform == "Saw" -> "Moog Grandmother"
                brightScore > 0.5f && waveform == "Square" -> "Korg MS-20"
                waveform == "Pulse" -> "Roland TB-303"
                warmScore > 0.5f -> "Sequential Prophet-6"
                else -> "Moog Sub 37"
            }
            "Mids" -> when {
                harmonicComplexity > 4 -> "Arturia MatrixBrute"
                warmScore > 0.6f && waveform == "Saw" -> "Roland JUNO-106"
                brightScore > 0.4f && waveform == "Saw" -> "Korg Minilogue XD"
                waveform == "Triangle" -> "Novation Peak"
                harmonicComplexity > 3 -> "Sequential Prophet-6"
                else -> "Korg Minilogue XD"
            }
            "Highs" -> when {
                brightScore > 0.6f -> "Korg Minilogue XD"
                harmonicComplexity > 5 -> "Arturia MatrixBrute"
                waveform == "Saw" && brightScore > 0.4f -> "Novation Peak"
                else -> "Roland JUNO-106"
            }
            else -> null
        }
    }

    private fun calculateWarmthScore(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0f

        val lowHarmonics = harmonics.take(3).sum()
        val highHarmonics = harmonics.drop(3).sum()
        val total = lowHarmonics + highHarmonics

        return if (total > 0) lowHarmonics / total else 0.5f
    }

    private fun calculateBrightnessScore(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0f

        val highHarmonics = harmonics.drop(4).sum()
        val total = harmonics.sum()

        return if (total > 0) highHarmonics / total else 0.3f
    }
}
