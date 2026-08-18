package com.synthlens.app.engine

import kotlin.math.*

data class StemSynthProfile(
    val stemName: String,
    val detectedSynth: String,
    val brand: String,
    val category: String,
    val confidence: Float,
    val waveformType: String,
    val filterType: String,
    val frequencyRange: String,
    val peakFrequency: Float,
    val harmonics: List<Float>,
    val thd: Float,
    val rmsLevel: Float,
    val energy: Float,
    val characteristics: Map<String, String>
)

class StemAnalyzer {

    private val synthProfiles = mapOf(
        "moog_grandmother" to SynthProfile(
            name = "Grandmother", brand = "Moog", category = "Semi-Modular Analog",
            favoredWaveforms = listOf("Saw", "Triangle"),
            freqRange = 30f..800f, warmthThreshold = 0.55f, brightnessRange = 0.2f..0.5f,
            harmonicDecayMax = 0.4f, filterType = "Moog Ladder LP 24dB",
            characteristics = mapOf("oscillators" to "2 VCO + Sub", "modulation" to "LFO → Pitch", "effects" to "Chorus, Reverb")
        ),
        "moog_sub37" to SynthProfile(
            name = "Subsequent 37", brand = "Moog", category = "Paraphonic Analog",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 30f..600f, warmthThreshold = 0.45f, brightnessRange = 0.3f..0.6f,
            harmonicDecayMax = 0.45f, filterType = "Moog Ladder LP 24dB (Multidrive)",
            characteristics = mapOf("oscillators" to "2 VCO + Sub + Noise", "modulation" to "Multidrive", "effects" to "Overdrive, Fuzz")
        ),
        "korg_ms20" to SynthProfile(
            name = "MS-20", brand = "Korg", category = "Monophonic Semi-Modular",
            favoredWaveforms = listOf("Square", "Saw"),
            freqRange = 25f..500f, warmthThreshold = 0.35f, brightnessRange = 0.5f..0.8f,
            harmonicDecayMax = 0.5f, filterType = "Korg35 HP/LP 12dB",
            characteristics = mapOf("oscillators" to "2 VCO", "modulation" to "EG2 → Cutoff", "effects" to "External Input")
        ),
        "korg_miniloguexd" to SynthProfile(
            name = "Minilogue XD", brand = "Korg", category = "Polyphonic Analog/Hybrid",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 60f..4000f, warmthThreshold = 0.4f, brightnessRange = 0.4f..0.7f,
            harmonicDecayMax = 0.5f, filterType = "Low-Pass 2/4-pole",
            characteristics = mapOf("oscillators" to "2 Analog + Digital Engine", "modulation" to "Multi-Engine", "effects" to "Chorus, Delay, Reverb")
        ),
        "roland_juno106" to SynthProfile(
            name = "JUNO-106", brand = "Roland", category = "Polyphonic Analog",
            favoredWaveforms = listOf("Saw"),
            freqRange = 60f..2000f, warmthThreshold = 0.5f, brightnessRange = 0.25f..0.5f,
            harmonicDecayMax = 0.45f, filterType = "IR3109 LP 24dB",
            characteristics = mapOf("oscillators" to "1 VCO + Sub + Noise", "modulation" to "LFO → PWM", "effects" to "Chorus CE-1")
        ),
        "roland_tb303" to SynthProfile(
            name = "TB-303", brand = "Roland", category = "Monophonic Analog Bass",
            favoredWaveforms = listOf("Pulse", "Saw"),
            freqRange = 30f..500f, warmthThreshold = 0.3f, brightnessRange = 0.4f..0.7f,
            harmonicDecayMax = 0.55f, filterType = "Diode Ladder LP 18dB",
            characteristics = mapOf("oscillators" to "1 VCO", "modulation" to "Accent/Slide", "effects" to "None")
        ),
        "sequential_prophet6" to SynthProfile(
            name = "Prophet-6", brand = "Sequential", category = "Polyphonic Analog",
            favoredWaveforms = listOf("Saw", "Triangle"),
            freqRange = 50f..3000f, warmthThreshold = 0.55f, brightnessRange = 0.3f..0.55f,
            harmonicDecayMax = 0.45f, filterType = "SSM2044 LP 24dB",
            characteristics = mapOf("oscillators" to "2 VCO per voice", "modulation" to "Poly-mod", "effects" to "BBD Delay")
        ),
        "novation_peak" to SynthProfile(
            name = "Peak", brand = "Novation", category = "Polyphonic Hybrid",
            favoredWaveforms = listOf("Triangle", "Saw"),
            freqRange = 100f..6000f, warmthThreshold = 0.4f, brightnessRange = 0.3f..0.6f,
            harmonicDecayMax = 0.5f, filterType = "Oxford Analogue LP/HP/BP",
            characteristics = mapOf("oscillators" to "3 NCO + Sub + Noise", "modulation" to "FM modulation", "effects" to "Reverb, Chorus")
        ),
        "arturia_matrixbrute" to SynthProfile(
            name = "MatrixBrute", brand = "Arturia", category = "Analog Monophonic",
            favoredWaveforms = listOf("Square", "Saw"),
            freqRange = 30f..2000f, warmthThreshold = 0.4f, brightnessRange = 0.45f..0.75f,
            harmonicDecayMax = 0.55f, filterType = "Steiner-Parker LP/HP/BP",
            characteristics = mapOf("oscillators" to "3 VCO + Metalizer", "modulation" to "16x16 Matrix", "effects" to "Analog Delay")
        ),
        "moog_matriarch" to SynthProfile(
            name = "Matriarch", brand = "Moog", category = "Semi-Modular Analog",
            favoredWaveforms = listOf("Saw", "Triangle", "Square"),
            freqRange = 30f..1000f, warmthThreshold = 0.6f, brightnessRange = 0.2f..0.5f,
            harmonicDecayMax = 0.4f, filterType = "Moog Ladder LP 24dB",
            characteristics = mapOf("oscillators" to "4 VCO (2 pairs)", "modulation" to "60 patch points", "effects" to "Stereo Analog Delay")
        ),
        "moog_subsequent25" to SynthProfile(
            name = "Subsequent 25", brand = "Moog", category = "Paraphonic Analog",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 40f..500f, warmthThreshold = 0.5f, brightnessRange = 0.3f..0.6f,
            harmonicDecayMax = 0.45f, filterType = "Moog Ladder LP 24dB (Multidrive)",
            characteristics = mapOf("oscillators" to "2 VCO + Sub + Noise", "modulation" to "Multidrive", "effects" to "Overdrive")
        ),
        "korg_prologue" to SynthProfile(
            name = "Prologue", brand = "Korg", category = "Polyphonic Analog/Hybrid",
            favoredWaveforms = listOf("Saw", "Square", "Triangle"),
            freqRange = 100f..4000f, warmthThreshold = 0.4f, brightnessRange = 0.3f..0.7f,
            harmonicDecayMax = 0.5f, filterType = "2-pole LP/HP/BP",
            characteristics = mapOf("oscillators" to "2 Analog VCO + Multi-Engine", "modulation" to "User oscillators", "effects" to "Effects program")
        ),
        "korg_wavestate" to SynthProfile(
            name = "wavestate", brand = "Korg", category = "Digital Wavesequencing",
            favoredWaveforms = listOf("Saw", "Square", "Triangle", "Sine"),
            freqRange = 80f..6000f, warmthThreshold = 0.3f, brightnessRange = 0.4f..0.8f,
            harmonicDecayMax = 0.6f, filterType = "Digital multi-mode",
            characteristics = mapOf("oscillators" to "Wave Sequencing 2.0", "modulation" to "Modulations", "effects" to "Effects")
        ),
        "korg_opsix" to SynthProfile(
            name = "opsix", brand = "Korg", category = "Digital FM",
            favoredWaveforms = listOf("Sine", "Triangle"),
            freqRange = 100f..5000f, warmthThreshold = 0.3f, brightnessRange = 0.3f..0.7f,
            harmonicDecayMax = 0.6f, filterType = "Digital multi-mode",
            characteristics = mapOf("oscillators" to "6-operator FM (14 algorithms)", "modulation" to "Operator ratios", "effects" to "Effects")
        ),
        "arturia_minibrute2s" to SynthProfile(
            name = "MiniBrute 2S", brand = "Arturia", category = "Semi-Modular Analog",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 30f..800f, warmthThreshold = 0.4f, brightnessRange = 0.4f..0.7f,
            harmonicDecayMax = 0.5f, filterType = "Steiner-Parker LP/HP/BP",
            characteristics = mapOf("oscillators" to "VCO + VCO (Metalizer)", "modulation" to "Patchbay, Sequencer", "effects" to "None")
        ),
        "arturia_polybrute" to SynthProfile(
            name = "PolyBrute", brand = "Arturia", category = "Polyphonic Analog",
            favoredWaveforms = listOf("Saw", "Triangle", "Square"),
            freqRange = 50f..3000f, warmthThreshold = 0.5f, brightnessRange = 0.3f..0.6f,
            harmonicDecayMax = 0.45f, filterType = "Steiner-Parker LP/HP/BP",
            characteristics = mapOf("oscillators" to "2 VCO per voice (Metalizer)", "modulation" to "FullTouch, Matrix", "effects" to "Analog Chorus, Delay")
        ),
        "novation_summit" to SynthProfile(
            name = "Summit", brand = "Novation", category = "Polyphonic Hybrid",
            favoredWaveforms = listOf("Saw", "Triangle", "Square"),
            freqRange = 100f..5000f, warmthThreshold = 0.4f, brightnessRange = 0.3f..0.65f,
            harmonicDecayMax = 0.5f, filterType = "Oxford Analogue LP/HP/BP",
            characteristics = mapOf("oscillators" to "3 NCO per voice", "modulation" to "FM, Ring Mod", "effects" to "Reverb, Chorus, Delay")
        ),
        "roland_system8" to SynthProfile(
            name = "SYSTEM-8", brand = "Roland", category = "Polyphonic Digital Modeling",
            favoredWaveforms = listOf("Saw", "Square", "Triangle"),
            freqRange = 50f..4000f, warmthThreshold = 0.4f, brightnessRange = 0.3f..0.7f,
            harmonicDecayMax = 0.5f, filterType = "ACB Digital LP/HP/BP",
            characteristics = mapOf("oscillators" to "3 Osc (PLUG-OUT models)", "modulation" to "Cross-mod, LFO", "effects" to "Effects")
        ),
        "sequential_obx8" to SynthProfile(
            name = "OB-X8", brand = "Sequential (Oberheim)", category = "Polyphonic Analog",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 40f..1500f, warmthThreshold = 0.6f, brightnessRange = 0.3f..0.55f,
            harmonicDecayMax = 0.4f, filterType = "SEM-style LP/HP/BP 12/24dB",
            characteristics = mapOf("oscillators" to "2 VCO per voice", "modulation" to "Cross-mod", "effects" to "None")
        ),
        "behringer_deepmind12" to SynthProfile(
            name = "DeepMind 12", brand = "Behringer", category = "Polyphonic Analog",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 60f..2000f, warmthThreshold = 0.5f, brightnessRange = 0.3f..0.6f,
            harmonicDecayMax = 0.45f, filterType = "CEM3340/AS2164 LP 24dB",
            characteristics = mapOf("oscillators" to "2 VCO + Sub + Noise", "modulation" to "Mod Matrix, WiFi", "effects" to "TC FX (M200)")
        ),
        "behringer_modeld" to SynthProfile(
            name = "Model D", brand = "Behringer", category = "Monophonic Analog (Minimoog Clone)",
            favoredWaveforms = listOf("Saw", "Square"),
            freqRange = 30f..500f, warmthThreshold = 0.7f, brightnessRange = 0.2f..0.45f,
            harmonicDecayMax = 0.35f, filterType = "Moog Ladder LP 24dB",
            characteristics = mapOf("oscillators" to "3 VCO + Sub", "modulation" to "LFO → Pitch, Glide", "effects" to "None")
        ),
        "elektron_analogfour" to SynthProfile(
            name = "Analog Four MKII", brand = "Elektron", category = "Analog Synthesizer/Sequencer",
            favoredWaveforms = listOf("Saw", "Square", "Triangle"),
            freqRange = 40f..3000f, warmthThreshold = 0.4f, brightnessRange = 0.3f..0.7f,
            harmonicDecayMax = 0.5f, filterType = "Analog multi-mode 2/4-pole",
            characteristics = mapOf("oscillators" to "2 VCO per track + Sub", "modulation" to "Parameter Locks", "effects" to "Overdrive, Reverb")
        ),
        "elektron_digitone" to SynthProfile(
            name = "Digitone", brand = "Elektron", category = "Digital FM Synthesizer",
            favoredWaveforms = listOf("Sine", "Triangle"),
            freqRange = 100f..5000f, warmthThreshold = 0.3f, brightnessRange = 0.3f..0.7f,
            harmonicDecayMax = 0.6f, filterType = "Digital multi-mode",
            characteristics = mapOf("oscillators" to "4-operator FM (8 algorithms)", "modulation" to "Arpeggiator, Seq", "effects" to "Reverb, Delay, Chorus")
        )
    )

    fun analyzeStem(stem: Stem, fullSpectrum: FloatArray): StemSynthProfile {
        val warmth = calculateWarmth(stem.harmonicProfile)
        val brightness = calculateBrightness(stem.harmonicProfile)
        val harmonicComplexity = calculateHarmonicComplexity(stem.harmonicProfile)
        val spectralCentroid = calculateSpectralCentroid(stem.spectrum)
        val thd = calculateTHD(stem.harmonicProfile)

        val scores = mutableMapOf<String, Float>()

        for ((key, profile) in synthProfiles) {
            var score = 0f

            if (stem.waveformType in profile.favoredWaveforms) {
                score += 0.3f
            }

            if (stem.peakFrequency in profile.freqRange) {
                val rangeMid = (profile.freqRange.start + profile.freqRange.endInclusive) / 2
                val dist = abs(stem.peakFrequency - rangeMid) / (profile.freqRange.endInclusive - profile.freqRange.start)
                score += (0.25f * (1f - dist.coerceIn(0f, 1f)))
            }

            if (warmth > profile.warmthThreshold) {
                score += 0.15f
            }

            if (brightness in profile.brightnessRange) {
                score += 0.15f
            }

            if (spectralCentroid in profile.freqRange) {
                score += 0.1f
            }

            val decay = if (stem.harmonicProfile.isNotEmpty() && stem.harmonicProfile[0] > 0) {
                stem.harmonicProfile.last() / stem.harmonicProfile[0]
            } else 0.5f

            if (decay < profile.harmonicDecayMax) {
                score += 0.05f
            }

            scores[key] = score.coerceIn(0f, 1f)
        }

        val bestMatch = scores.maxByOrNull { it.value }
        val bestScore = bestMatch?.value ?: 0f
        val profile = bestMatch?.let { synthProfiles[it.key] }

        return if (bestScore > 0.3f && profile != null) {
            StemSynthProfile(
                stemName = stem.name,
                detectedSynth = profile.name,
                brand = profile.brand,
                category = profile.category,
                confidence = bestScore.coerceIn(0f, 1f),
                waveformType = stem.waveformType,
                filterType = profile.filterType,
                frequencyRange = stem.frequencyRange,
                peakFrequency = stem.peakFrequency,
                harmonics = stem.harmonicProfile,
                thd = thd,
                rmsLevel = stem.rmsLevel,
                energy = stem.energy,
                characteristics = profile.characteristics
            )
        } else {
            StemSynthProfile(
                stemName = stem.name,
                detectedSynth = "Unknown Synth",
                brand = "Unknown",
                category = "Analog",
                confidence = bestScore.coerceIn(0f, 1f),
                waveformType = stem.waveformType,
                filterType = "Unknown",
                frequencyRange = stem.frequencyRange,
                peakFrequency = stem.peakFrequency,
                harmonics = stem.harmonicProfile,
                thd = thd,
                rmsLevel = stem.rmsLevel,
                energy = stem.energy,
                characteristics = emptyMap()
            )
        }
    }

    private fun calculateWarmth(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0f

        val low = harmonics.take(3).sum()
        val high = harmonics.drop(3).sum()
        val total = low + high

        return if (total > 0) low / total else 0.5f
    }

    private fun calculateBrightness(harmonics: List<Float>): Float {
        if (harmonics.isEmpty()) return 0f
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0f

        val high = harmonics.drop(4).sum()
        val total = harmonics.sum()

        return if (total > 0) high / total else 0.3f
    }

    private fun calculateHarmonicComplexity(harmonics: List<Float>): Int {
        if (harmonics.isEmpty()) return 0
        val fundamental = harmonics[0]
        if (fundamental <= 0) return 0

        return harmonics.count { it > fundamental * 0.1f }
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

    private fun calculateTHD(harmonics: List<Float>): Float {
        if (harmonics.size < 2 || harmonics[0] <= 0) return 0f
        var thdSum = 0f
        for (i in 1 until harmonics.size) {
            thdSum += harmonics[i] * harmonics[i]
        }
        return sqrt(thdSum) / harmonics[0]
    }

    private data class SynthProfile(
        val name: String,
        val brand: String,
        val category: String,
        val favoredWaveforms: List<String>,
        val freqRange: ClosedFloatingPointRange<Float>,
        val warmthThreshold: Float,
        val brightnessRange: ClosedFloatingPointRange<Float>,
        val harmonicDecayMax: Float,
        val filterType: String,
        val characteristics: Map<String, String>
    )
}
