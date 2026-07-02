package com.example.myapplication.engine

import kotlin.math.*

data class DetectedHandpanResult(
    val instrumentName: String,
    val brand: String,
    val model: String,
    val confidence: Float,
    val detectedNote: String,
    val detectedOctave: Int,
    val fundamentalHz: Float,
    val centsOffset: Int,
    val harmonicCount: Int,
    val harmonics: List<HarmonicInfo>,
    val spectralProfile: String,
    val sustainRating: String,
    val attackType: String,
    val material: String,
    val scale: String,
    val tuningSystem: String,
    val sizeCategory: String,
    val overtoneRatio: Float,
    val inharmonicity: Float,
    val brightnessIndex: Float,
    val warmthIndex: Float,
    val spectralCentroid: Float,
    val spectralFlatness: Float
)

data class HarmonicInfo(
    val harmonicNumber: Int,
    val frequency: Float,
    val amplitudeRatio: Float,
    val type: String,
    val centsFromPure: Int
)

data class HandpanProfile(
    val name: String,
    val brand: String,
    val model: String,
    val fundamentalRange: ClosedFloatingPointRange<Float>,
    val octaveRatio: Float,
    val fifthRatio: Float,
    val expectedPartials: List<Float>,
    val spectralFlatnessRange: ClosedFloatingPointRange<Float>,
    val material: String,
    val scale: String,
    val tuningSystem: String,
    val sizeCategory: String
)

class HandpanDetector {

    companion object {
        val NOTE_NAMES = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")

        val INSTRUMENT_PROFILES = listOf(
            HandpanProfile("Hang", "Hang", "Original", 100f..600f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f),
                0.15f..0.45f, "Nitrided Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Halo", "Spce", "Halo", 120f..550f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f),
                0.12f..0.40f, "Nitrided Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Rav Vast", "Rav", "Vast", 100f..500f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 2.997f, 4.0f),
                0.10f..0.38f, "Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Halo+", "Spce", "Halo+", 130f..520f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f),
                0.13f..0.42f, "Nitrided Steel", "Various", "440 Hz Equal", "Large"),
            HandpanProfile(" Saraz", "Saraz", "Classic", 110f..480f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 3.0f),
                0.14f..0.43f, "Hand-hammered Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Halo Mini", "Spce", "Mini", 180f..700f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f),
                0.18f..0.50f, "Steel", "Various", "440 Hz Equal", "Mini"),
            HandpanProfile(" Caisa", "Caisa", "Standard", 120f..500f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f),
                0.12f..0.42f, "Nitrided Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Guda", "Guda", "Drum", 100f..450f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f),
                0.16f..0.48f, "Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Halo Pro", "Spce", "Pro", 90f..580f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f),
                0.10f..0.35f, "Premium Nitrided Steel", "Various", "440 Hz Equal", "Large"),
            HandpanProfile(" Yata", "Yata", "Standard", 130f..520f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 3.0f),
                0.15f..0.44f, "Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Panart", "Panart", "Symphony", 100f..550f, 2.0f, 1.5f,
                listOf(1.0f, 2.0f, 3.0f, 4.0f),
                0.11f..0.39f, "Nitrided Steel", "Various", "440 Hz Equal", "Standard"),
            HandpanProfile(" Generic Handpan", "Unknown", "Handpan", 80f..700f, 2.0f, 1.498f,
                listOf(1.0f, 2.0f, 3.0f),
                0.08f..0.55f, "Steel", "Various", "440 Hz Equal", "Various")
        )
    }

    fun detect(
        frequency: Float,
        amplitude: Float,
        harmonics: List<Float>,
        spectrum: FloatArray,
        waveformType: String,
        spectralFlatness: Float,
        spectralRolloff: Float,
        spectralBandwidth: Float,
        hnr: Float,
        thd: Float
    ): DetectedHandpanResult? {
        if (frequency < 100f || frequency > 700f || amplitude < 0.06f) return null
        if (harmonics.size < 3 || harmonics[0] < 0.002f) return null

        val isHandpanTimbre = evaluateHandpanTimbre(
            frequency, harmonics, spectrum, spectralFlatness, thd, waveformType
        )
        if (isHandpanTimbre < 0.25f) return null

        val bestProfile = matchInstrumentProfile(frequency, harmonics, spectralFlatness)
        val harmonicInfoList = analyzeHarmonicsDetailed(frequency, harmonics, spectrum)
        val noteResult = frequencyToNote(frequency)
        val overtoneRatio = calculateOvertoneRatio(harmonics)
        val inharmonicity = calculateInharmonicity(harmonics, frequency)
        val brightness = calculateBrightnessIndex(harmonics)
        val warmth = 1f - brightness
        val spectralCentroid = calculateSpectralCentroid(spectrum)
        val sustain = evaluateSustain(harmonics)
        val attack = evaluateAttack(waveformType, thd)

        val confidence = (isHandpanTimbre * 0.35f +
                overtoneRatio * 0.15f +
                (1f - inharmonicity.coerceIn(0f, 1f)) * 0.15f +
                (if (frequency in bestProfile.fundamentalRange) 0.2f else 0.05f) +
                (if (spectralFlatness in bestProfile.spectralFlatnessRange) 0.15f else 0.05f)
        ).coerceIn(0f, 1f)

        if (confidence < 0.45f) return null

        return DetectedHandpanResult(
            instrumentName = bestProfile.name,
            brand = bestProfile.brand,
            model = bestProfile.model,
            confidence = confidence,
            detectedNote = noteResult.noteName,
            detectedOctave = noteResult.octave,
            fundamentalHz = frequency,
            centsOffset = noteResult.cents,
            harmonicCount = harmonicInfoList.size,
            harmonics = harmonicInfoList,
            spectralProfile = describeSpectralProfile(spectralFlatness, spectralRolloff, spectralBandwidth),
            sustainRating = sustain,
            attackType = attack,
            material = bestProfile.material,
            scale = bestProfile.scale,
            tuningSystem = bestProfile.tuningSystem,
            sizeCategory = bestProfile.sizeCategory,
            overtoneRatio = overtoneRatio,
            inharmonicity = inharmonicity,
            brightnessIndex = brightness,
            warmthIndex = warmth,
            spectralCentroid = spectralCentroid,
            spectralFlatness = spectralFlatness
        )
    }

    private fun evaluateHandpanTimbre(
        frequency: Float,
        harmonics: List<Float>,
        spectrum: FloatArray,
        spectralFlatness: Float,
        thd: Float,
        waveformType: String
    ): Float {
        var score = 0f

        if (frequency in 80f..700f) score += 0.2f
        else if (frequency in 50f..900f) score += 0.1f

        if (harmonics.size >= 3) {
            val h1 = harmonics[0]
            if (h1 > 0) {
                val h2Ratio = harmonics.getOrNull(1)?.let { it / h1 } ?: 0f
                val h3Ratio = harmonics.getOrNull(2)?.let { it / h1 } ?: 0f

                if (h2Ratio in 0.6f..1.4f) score += 0.25f
                else if (h2Ratio in 0.3f..1.8f) score += 0.1f

                if (h3Ratio in 0.3f..1.0f) score += 0.2f
                else if (h3Ratio > 0.15f) score += 0.08f
            }
        }

        if (spectralFlatness in 0.1f..0.5f) score += 0.15f

        if (thd in 0.05f..0.6f) score += 0.1f

        if (waveformType == "Sine" || waveformType == "Triangle") score += 0.1f

        if (harmonics.size in 3..8) score += 0.1f

        return score.coerceIn(0f, 1f)
    }

    private fun matchInstrumentProfile(
        frequency: Float,
        harmonics: List<Float>,
        spectralFlatness: Float
    ): HandpanProfile {
        var bestScore = -1f
        var bestProfile = INSTRUMENT_PROFILES.last()

        for (profile in INSTRUMENT_PROFILES) {
            var score = 0f

            if (frequency in profile.fundamentalRange) {
                val rangeMid = (profile.fundamentalRange.start + profile.fundamentalRange.endInclusive) / 2
                val dist = abs(frequency - rangeMid) / (profile.fundamentalRange.endInclusive - profile.fundamentalRange.start)
                score += (0.4f * (1f - dist.coerceIn(0f, 1f)))
            }

            if (spectralFlatness in profile.spectralFlatnessRange) score += 0.3f

            if (harmonics.size >= 3) {
                val h1 = harmonics[0]
                if (h1 > 0) {
                    val h2Ratio = harmonics.getOrNull(1)?.let { it / h1 } ?: 0f
                    val expectedOctave = profile.octaveRatio
                    if (abs(h2Ratio - expectedOctave) < 0.3f) score += 0.3f
                }
            }

            if (score > bestScore) {
                bestScore = score
                bestProfile = profile
            }
        }

        return bestProfile
    }

    private fun analyzeHarmonicsDetailed(
        fundamental: Float,
        harmonics: List<Float>,
        spectrum: FloatArray
    ): List<HarmonicInfo> {
        val result = mutableListOf<HarmonicInfo>()
        if (harmonics.isEmpty() || fundamental <= 0) return result

        for (i in harmonics.indices) {
            val harmonicNum = i + 1
            val expectedFreq = fundamental * harmonicNum
            val actualFreq = if (spectrum.isNotEmpty()) {
                findPeakFrequency(spectrum, expectedFreq)
            } else expectedFreq

            val amplitudeRatio = if (harmonics[0] > 0) harmonics[i] / harmonics[0] else 0f

            val centsFromPure = if (actualFreq > 0 && expectedFreq > 0) {
                (1200 * log2(actualFreq / expectedFreq)).roundToInt()
            } else 0

            val type = when {
                harmonicNum == 1 -> "Fundamental"
                harmonicNum == 2 -> "Octave"
                harmonicNum == 3 -> "Octave + 5th"
                harmonicNum == 4 -> "Two Octaves"
                harmonicNum == 5 -> "Octave + Major 3rd"
                harmonicNum == 6 -> "Octave + 5th + Octave"
                harmonicNum == 7 -> "Minor 7th Partial"
                harmonicNum == 8 -> "Three Octaves"
                else -> "Upper Partial"
            }

            if (amplitudeRatio > 0.05f) {
                result.add(HarmonicInfo(
                    harmonicNumber = harmonicNum,
                    frequency = actualFreq,
                    amplitudeRatio = amplitudeRatio,
                    type = type,
                    centsFromPure = centsFromPure
                ))
            }
        }

        return result
    }

    private fun findPeakFrequency(spectrum: FloatArray, targetFreq: Float): Float {
        if (spectrum.isEmpty()) return targetFreq
        val binWidth = 44100f / (spectrum.size * 2)
        val targetBin = (targetFreq / binWidth).toInt().coerceIn(0, spectrum.size - 1)
        val searchRange = 5
        var maxBin = targetBin
        var maxVal = 0f

        for (i in maxOf(0, targetBin - searchRange)..minOf(spectrum.size - 1, targetBin + searchRange)) {
            if (spectrum[i] > maxVal) {
                maxVal = spectrum[i]
                maxBin = i
            }
        }

        return maxBin * binWidth
    }

    private fun frequencyToNote(freq: Float): NoteResult {
        if (freq <= 0) return NoteResult("---", 0, 0)
        val noteNames = arrayOf("C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B")
        val midiNote = (69 + 12 * log2(freq / 440f)).roundToInt()
        val noteIndex = midiNote % 12
        val octave = (midiNote / 12) - 1
        val expectedFreq = 440f * 2f.pow((midiNote - 69) / 12f)
        val cents = ((1200 * log2(freq / expectedFreq))).roundToInt()
        return NoteResult(noteNames[noteIndex], octave, cents)
    }

    private data class NoteResult(val noteName: String, val octave: Int, val cents: Int)

    private fun calculateOvertoneRatio(harmonics: List<Float>): Float {
        if (harmonics.size < 2 || harmonics[0] <= 0) return 0f
        val octaveStrength = harmonics.getOrNull(1)?.let { it / harmonics[0] } ?: 0f
        val fifthStrength = harmonics.getOrNull(2)?.let { it / harmonics[0] } ?: 0f
        return (octaveStrength * 0.6f + fifthStrength * 0.4f).coerceIn(0f, 1f)
    }

    private fun calculateInharmonicity(harmonics: List<Float>, fundamental: Float): Float {
        if (harmonics.size < 3 || fundamental <= 0) return 0f
        var totalDeviation = 0f
        var count = 0

        for (i in 1 until harmonics.size) {
            val expectedRatio = (i + 1).toFloat()
            val actualAmplitude = harmonics[i]
            if (actualAmplitude > harmonics[0] * 0.05f) {
                count++
            }
        }

        if (count == 0) return 0f

        for (i in 1 until harmonics.size) {
            val expectedRatio = (i + 1).toFloat()
            val deviation = abs(1f - (harmonics[i] / harmonics[0]).coerceIn(0f, 2f) / expectedRatio)
            totalDeviation += deviation
        }

        return (totalDeviation / harmonics.size).coerceIn(0f, 1f)
    }

    private fun calculateBrightnessIndex(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val total = harmonics.sum()
        if (total <= 0) return 0f
        val highEnergy = harmonics.drop(3).sum()
        return (highEnergy / total).coerceIn(0f, 1f)
    }

    private fun calculateSpectralCentroid(spectrum: FloatArray): Float {
        if (spectrum.isEmpty()) return 0f
        var weightedSum = 0f
        var totalMag = 0f
        for (i in spectrum.indices) {
            val freq = (i.toFloat() / spectrum.size) * 22050f
            weightedSum += freq * spectrum[i]
            totalMag += spectrum[i]
        }
        return if (totalMag > 0) weightedSum / totalMag else 0f
    }

    private fun evaluateSustain(harmonics: List<Float>): String {
        if (harmonics.size < 4) return "Short"
        val decayRate = if (harmonics[0] > 0) harmonics.last() / harmonics[0] else 0f
        return when {
            decayRate > 0.4f -> "Very Long"
            decayRate > 0.25f -> "Long"
            decayRate > 0.15f -> "Medium"
            else -> "Short"
        }
    }

    private fun evaluateAttack(waveformType: String, thd: Float): String {
        return when {
            waveformType == "Sine" && thd < 0.15f -> "Soft (Hand)"
            waveformType == "Triangle" -> "Medium (Hand/Finger)"
            thd > 0.4f -> "Hard (Mallet)"
            waveformType == "Saw" -> "Hard (Stick)"
            else -> "Medium (Hand)"
        }
    }

    private fun describeSpectralProfile(flatness: Float, rolloff: Float, bandwidth: Float): String {
        val parts = mutableListOf<String>()
        parts.add(when {
            flatness < 0.2f -> "Tonal"
            flatness < 0.4f -> "Harmonic"
            else -> "Complex"
        })
        parts.add(when {
            rolloff < 2000f -> "Warm"
            rolloff < 5000f -> "Balanced"
            else -> "Bright"
        })
        parts.add(when {
            bandwidth < 1000f -> "Focused"
            bandwidth < 3000f -> "Moderate"
            else -> "Wide"
        })
        return parts.joinToString(" · ")
    }
}
