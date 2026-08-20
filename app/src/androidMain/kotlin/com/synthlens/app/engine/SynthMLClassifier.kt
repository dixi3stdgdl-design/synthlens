package com.synthlens.app.engine

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

data class MLClassificationResult(
    val synthName: String,
    val brand: String,
    val category: String,
    val confidence: Float,
    val allScores: Map<String, Float> = emptyMap(),
    val modelUsed: String = "heuristic"
)

class SynthMLClassifier(private val context: Context) {

    private var tfliteInterpreter: Interpreter? = null
    private var isModelLoaded = false

    private val synthLabels = listOf(
        "moog_grandmother", "moog_sub37", "moog_model_d", "moog_one", "moog_matriarch", "moog_subsequent25",
        "korg_ms20", "korg_minilogue_xd", "korg_monologue", "korg_wavestation", "korg_prologue", "korg_opsix",
        "roland_juno106", "roland_tb303", "roland_sh101", "roland_jupiter8", "roland_system8",
        "sequential_prophet6", "sequential_prophet5", "sequential_pro3", "sequential_obx8",
        "novation_peak", "novation_bassstation2", "novation_summit",
        "arturia_matrixbrute", "arturia_microfreak", "arturia_minibrute2s", "arturia_polybrute",
        "behringer_td3", "behringer_model_d", "behringer_deepmind12",
        "yamaha_dx7", "yamaha_montage",
        "waldorf_iriidium", "waldorf_blofeld",
        "elektron_digitakt", "elektron_digitone", "elektron_analog_four",
        "teenage_op1_field",
        "software_sylenth1", "software_serum", "software_vital", "software_massive",
        "generic_analog", "generic_digital", "generic_fm", "unknown"
    )

    init {
        loadTFLiteModel()
    }

    private fun loadTFLiteModel() {
        try {
            val modelBuffer = loadModelFile("synth_model.tflite")
            if (modelBuffer != null) {
                val options = Interpreter.Options().apply {
                    setNumThreads(2)
                }
                tfliteInterpreter = Interpreter(modelBuffer, options)
                isModelLoaded = true
            }
        } catch (e: Exception) {
            isModelLoaded = false
        }
    }

    private fun loadModelFile(filename: String): MappedByteBuffer? {
        return try {
            val fileDescriptor = context.assets.openFd(filename)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            fileChannel.map(
                FileChannel.MapMode.READ_ONLY,
                fileDescriptor.startOffset,
                fileDescriptor.declaredLength
            )
        } catch (e: Exception) {
            null
        }
    }

    fun classify(features: SynthFeatures): MLClassificationResult? {
        if (features.amplitude < 0.08f || features.frequency < 30f) return null
        if (features.spectralCentroid <= 0f) return null

        if (isModelLoaded && tfliteInterpreter != null) {
            return classifyWithTFLite(features)
        }

        return classifyWithHeuristic(features)
    }

    private fun classifyWithTFLite(features: SynthFeatures): MLClassificationResult? {
        return try {
            val input = featuresToByteBuffer(features)
            val output = Array(1) { FloatArray(synthLabels.size) }

            tfliteInterpreter?.run(input, output)

            val scores = output[0]
            val maxIdx = scores.indices.maxByOrNull { scores[it] } ?: return null
            val confidence = scores[maxIdx]

            if (confidence < 0.3f) return null

            val label = synthLabels[maxIdx]
            val allScores = synthLabels.zip(scores.toList()).toMap()

            MLClassificationResult(
                synthName = formatLabel(label),
                brand = extractBrand(label),
                category = extractCategory(label),
                confidence = confidence,
                allScores = allScores,
                modelUsed = "tflite"
            )
        } catch (e: Exception) {
            classifyWithHeuristic(features)
        }
    }

    private fun featuresToByteBuffer(features: SynthFeatures): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(18 * 4)
        buffer.order(ByteOrder.nativeOrder())

        buffer.putFloat(features.frequency)
        buffer.putFloat(features.amplitude)
        buffer.putFloat(features.spectralCentroid)
        buffer.putFloat(features.spectralFlatness)
        buffer.putFloat(features.spectralRolloff)
        buffer.putFloat(features.spectralBandwidth)
        buffer.putFloat(features.harmonicToNoiseRatio)
        buffer.putFloat(features.thd)
        buffer.putFloat(features.waveformSine)
        buffer.putFloat(features.waveformSaw)
        buffer.putFloat(features.waveformSquare)
        buffer.putFloat(features.waveformTriangle)
        buffer.putFloat(features.waveformPulse)
        buffer.putFloat(features.oddEvenRatio)
        buffer.putFloat(features.harmonicDecay)
        buffer.putFloat(features.brightness)
        buffer.putFloat(features.warmth)
        buffer.putFloat(features.octave.toFloat())

        buffer.rewind()
        return buffer
    }

    private fun classifyWithHeuristic(features: SynthFeatures): MLClassificationResult {
        val scores = mutableMapOf<String, Float>()

        scores["moog_grandmother"] = scoreMoogHeuristic(features, "warm", "saw")
        scores["moog_sub37"] = scoreMoogHeuristic(features, "mid", "saw_square")
        scores["moog_model_d"] = scoreMoogHeuristic(features, "warm", "saw_square")
        scores["moog_one"] = scoreMoogHeuristic(features, "warm", "saw_square_triangle")
        scores["moog_matriarch"] = scoreMoogHeuristic(features, "warm", "saw_triangle_square")
        scores["moog_subsequent25"] = scoreMoogHeuristic(features, "mid", "saw_square")

        scores["korg_ms20"] = scoreKorgHeuristic(features, "bright", "square_saw")
        scores["korg_minilogue_xd"] = scoreKorgHeuristic(features, "mid", "saw_square")
        scores["korg_monologue"] = scoreKorgHeuristic(features, "bright", "saw_square")
        scores["korg_prologue"] = scoreKorgHeuristic(features, "mid", "saw_square_triangle")
        scores["korg_wavestate"] = scoreDigitalHeuristic(features)
        scores["korg_opsix"] = scoreFMHeuristic(features)

        scores["roland_juno106"] = scoreRolandHeuristic(features, "warm", "saw")
        scores["roland_tb303"] = scoreRolandHeuristic(features, "bright", "pulse_saw")
        scores["roland_sh101"] = scoreRolandHeuristic(features, "mid", "saw_square")
        scores["roland_jupiter8"] = scoreRolandHeuristic(features, "mid", "saw_square_triangle")
        scores["roland_system8"] = scoreDigitalHeuristic(features)

        scores["sequential_prophet6"] = scoreSequentialHeuristic(features, "warm", "saw_triangle")
        scores["sequential_prophet5"] = scoreSequentialHeuristic(features, "warm", "saw_pulse")
        scores["sequential_pro3"] = scoreSequentialHeuristic(features, "mid", "saw_square_triangle")
        scores["sequential_obx8"] = scoreSequentialHeuristic(features, "warm", "saw_square")

        scores["novation_peak"] = scoreNovationHeuristic(features, "mid", "triangle_saw")
        scores["novation_bassstation2"] = scoreNovationHeuristic(features, "mid", "saw_square_pulse")
        scores["novation_summit"] = scoreNovationHeuristic(features, "mid", "saw_triangle_square")

        scores["arturia_matrixbrute"] = scoreArturiaHeuristic(features, "bright", "square_saw")
        scores["arturia_microfreak"] = scoreArturiaHeuristic(features, "bright", "any")
        scores["arturia_minibrute2s"] = scoreArturiaHeuristic(features, "mid", "saw_square")
        scores["arturia_polybrute"] = scoreArturiaHeuristic(features, "warm", "saw_triangle_square")

        scores["behringer_td3"] = scoreBehringerHeuristic(features, "bright", "saw_square")
        scores["behringer_model_d"] = scoreBehringerHeuristic(features, "warm", "saw_square")
        scores["behringer_deepmind12"] = scoreBehringerHeuristic(features, "warm", "saw_square")

        scores["yamaha_dx7"] = scoreFMHeuristic(features)
        scores["yamaha_montage"] = scoreDigitalHeuristic(features)

        scores["waldorf_iriidium"] = scoreWavetableHeuristic(features)
        scores["waldorf_blofeld"] = scoreWavetableHeuristic(features)

        scores["elektron_digitakt"] = scoreSamplerHeuristic(features)
        scores["elektron_digitone"] = scoreFMHeuristic(features)
        scores["elektron_analogfour"] = scoreAnalogSeqHeuristic(features)

        scores["software_sylenth1"] = scoreSoftwareHeuristic(features)
        scores["software_serum"] = scoreWavetableHeuristic(features)
        scores["software_vital"] = scoreWavetableHeuristic(features)
        scores["software_massive"] = scoreWavetableHeuristic(features)

        val bestMatch = scores.maxByOrNull { it.value }
        val bestScore = bestMatch?.value ?: 0f

        return if (bestScore > 0.4f && bestMatch != null) {
            MLClassificationResult(
                synthName = formatLabel(bestMatch.key),
                brand = extractBrand(bestMatch.key),
                category = extractCategory(bestMatch.key),
                confidence = bestScore.coerceIn(0f, 1f),
                allScores = scores,
                modelUsed = "heuristic"
            )
        } else {
            MLClassificationResult(
                synthName = "Unknown Synth",
                brand = "Unknown",
                category = "Analog",
                confidence = 0.3f,
                allScores = scores,
                modelUsed = "heuristic"
            )
        }
    }

    private fun scoreMoogHeuristic(f: SynthFeatures, warmth: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.25f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.2f
        if (warmth == "warm" && f.warmth > 0.6f) score += 0.2f
        if (warmth == "mid" && f.warmth in 0.4f..0.6f) score += 0.15f
        if (f.frequency in 30f..800f) score += 0.15f
        if (f.spectralCentroid in 200f..1500f) score += 0.1f
        return score
    }

    private fun scoreKorgHeuristic(f: SynthFeatures, brightness: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.2f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.15f
        if (brightness == "bright" && f.brightness > 0.5f) score += 0.2f
        if (brightness == "mid" && f.brightness in 0.3f..0.6f) score += 0.15f
        if (f.frequency in 40f..3000f) score += 0.15f
        if (f.spectralCentroid in 300f..2500f) score += 0.1f
        return score
    }

    private fun scoreRolandHeuristic(f: SynthFeatures, brightness: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.25f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("pulse") && f.waveformPulse > 0.5f) score += 0.25f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.15f
        if (brightness == "bright" && f.brightness > 0.4f) score += 0.2f
        if (brightness == "warm" && f.warmth > 0.5f) score += 0.2f
        if (brightness == "mid" && f.brightness in 0.3f..0.6f) score += 0.15f
        if (f.frequency in 40f..2000f) score += 0.15f
        return score
    }

    private fun scoreSequentialHeuristic(f: SynthFeatures, warmth: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.25f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.2f
        if (waveforms.contains("pulse") && f.waveformPulse > 0.5f) score += 0.2f
        if (warmth == "warm" && f.warmth > 0.55f) score += 0.2f
        if (warmth == "mid" && f.warmth in 0.4f..0.55f) score += 0.15f
        if (f.frequency in 50f..2000f) score += 0.15f
        return score
    }

    private fun scoreNovationHeuristic(f: SynthFeatures, brightness: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.2f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.2f
        if (waveforms.contains("pulse") && f.waveformPulse > 0.5f) score += 0.15f
        if (brightness == "mid" && f.brightness in 0.3f..0.6f) score += 0.2f
        if (brightness == "bright" && f.brightness > 0.4f) score += 0.15f
        if (f.frequency in 100f..5000f) score += 0.15f
        if (f.spectralCentroid in 400f..3000f) score += 0.1f
        return score
    }

    private fun scoreArturiaHeuristic(f: SynthFeatures, brightness: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.2f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (waveforms.contains("triangle") && f.waveformTriangle > 0.5f) score += 0.15f
        if (waveforms.contains("any")) score += 0.1f
        if (brightness == "bright" && f.brightness > 0.4f) score += 0.2f
        if (brightness == "warm" && f.warmth > 0.5f) score += 0.2f
        if (brightness == "mid" && f.brightness in 0.3f..0.6f) score += 0.15f
        if (f.frequency in 30f..3000f) score += 0.15f
        return score
    }

    private fun scoreBehringerHeuristic(f: SynthFeatures, warmth: String, waveforms: String): Float {
        var score = 0f
        if (waveforms.contains("saw") && f.waveformSaw > 0.5f) score += 0.25f
        if (waveforms.contains("square") && f.waveformSquare > 0.5f) score += 0.2f
        if (warmth == "warm" && f.warmth > 0.5f) score += 0.2f
        if (warmth == "bright" && f.brightness > 0.4f) score += 0.15f
        if (f.frequency in 30f..1500f) score += 0.15f
        if (f.spectralCentroid in 200f..1500f) score += 0.1f
        return score
    }

    private fun scoreFMHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.waveformSine > 0.5f || f.waveformTriangle > 0.5f) score += 0.25f
        if (f.brightness > 0.3f) score += 0.2f
        if (f.frequency in 100f..5000f) score += 0.15f
        if (f.spectralCentroid in 300f..4000f) score += 0.2f
        if (f.oddEvenRatio in 0.2f..0.7f) score += 0.15f
        return score
    }

    private fun scoreDigitalHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.waveformSaw > 0.3f || f.waveformSquare > 0.3f || f.waveformTriangle > 0.3f) score += 0.15f
        if (f.brightness > 0.3f) score += 0.2f
        if (f.frequency in 80f..6000f) score += 0.15f
        if (f.spectralCentroid in 400f..5000f) score += 0.2f
        if (f.spectralFlatness > 0.2f) score += 0.1f
        return score
    }

    private fun scoreWavetableHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.waveformSaw > 0.3f || f.waveformSquare > 0.3f) score += 0.15f
        if (f.brightness > 0.35f) score += 0.2f
        if (f.frequency in 100f..6000f) score += 0.15f
        if (f.spectralCentroid in 400f..5000f) score += 0.2f
        if (f.spectralFlatness > 0.25f) score += 0.1f
        return score
    }

    private fun scoreSamplerHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.amplitude > 0.1f) score += 0.15f
        if (f.spectralFlatness > 0.3f) score += 0.2f
        if (f.frequency in 50f..8000f) score += 0.15f
        if (f.thd > 0.1f) score += 0.1f
        return score
    }

    private fun scoreAnalogSeqHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.waveformSaw > 0.4f || f.waveformSquare > 0.4f || f.waveformTriangle > 0.4f) score += 0.2f
        if (f.warmth > 0.4f) score += 0.2f
        if (f.frequency in 40f..3000f) score += 0.15f
        if (f.spectralCentroid in 300f..2500f) score += 0.15f
        return score
    }

    private fun scoreSoftwareHeuristic(f: SynthFeatures): Float {
        var score = 0f
        if (f.spectralFlatness > 0.3f) score += 0.2f
        if (f.brightness > 0.3f) score += 0.15f
        if (f.frequency in 100f..8000f) score += 0.15f
        if (f.spectralCentroid in 500f..6000f) score += 0.2f
        return score
    }

    private fun formatLabel(label: String): String {
        return label.split("_").joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
    }

    private fun extractBrand(label: String): String {
        val brandMap = mapOf(
            "moog" to "Moog", "korg" to "Korg", "roland" to "Roland",
            "sequential" to "Sequential", "novation" to "Novation",
            "arturia" to "Arturia", "behringer" to "Behringer",
            "yamaha" to "Yamaha", "waldorf" to "Waldorf",
            "elektron" to "Elektron", "teenage" to "Teenage Engineering",
            "software" to "Software Synth"
        )
        return brandMap[label.split("_").first()] ?: "Unknown"
    }

    private fun extractCategory(label: String): String {
        return when {
            label.contains("software") -> "Software"
            label.contains("dx7") || label.contains("montage") -> "FM Digital"
            label.contains("iriidium") || label.contains("blofeld") -> "Wavetable"
            label.contains("digitakt") -> "Sampler/Drum"
            label.contains("digitone") -> "FM Digital"
            label.contains("op1") -> "Hybrid Digital"
            else -> "Analog"
        }
    }

    fun close() {
        tfliteInterpreter?.close()
        tfliteInterpreter = null
        isModelLoaded = false
    }
}

data class SynthFeatures(
    val frequency: Float = 0f,
    val amplitude: Float = 0f,
    val spectralCentroid: Float = 0f,
    val spectralFlatness: Float = 0f,
    val spectralRolloff: Float = 0f,
    val spectralBandwidth: Float = 0f,
    val harmonicToNoiseRatio: Float = 0f,
    val thd: Float = 0f,
    val waveformSine: Float = 0f,
    val waveformSaw: Float = 0f,
    val waveformSquare: Float = 0f,
    val waveformTriangle: Float = 0f,
    val waveformPulse: Float = 0f,
    val oddEvenRatio: Float = 0f,
    val harmonicDecay: Float = 0f,
    val brightness: Float = 0f,
    val warmth: Float = 0f,
    val octave: Int = 0
)

fun AudioAnalysis.toSynthFeatures(): SynthFeatures {
    val waveformSine = if (waveformType == "Sine") 1f else 0f
    val waveformSaw = if (waveformType == "Saw") 1f else 0f
    val waveformSquare = if (waveformType == "Square") 1f else 0f
    val waveformTriangle = if (waveformType == "Triangle") 1f else 0f
    val waveformPulse = if (waveformType == "Pulse") 1f else 0f

    val oddEvenRatio = if (harmonics.size >= 2) {
        var odd = 0f; var even = 0f
        harmonics.forEachIndexed { i, v -> if (i % 2 == 0) odd += v else even += v }
        if (odd + even > 0) odd / (odd + even) else 0.5f
    } else 0.5f

    val harmonicDecay = if (harmonics.size >= 2 && harmonics[0] > 0) {
        harmonics.last() / harmonics[0]
    } else 0.5f

    val totalHarmonics = harmonics.sum().coerceAtLeast(0.001f)
    val highHarmonics = harmonics.drop(4).sum()
    val brightness = highHarmonics / totalHarmonics
    val warmth = 1f - brightness

    return SynthFeatures(
        frequency = frequency,
        amplitude = amplitude,
        spectralCentroid = if (spectrumData.isNotEmpty()) { var ws = 0f; var tm = 0f; for (i in spectrumData.indices) { ws += i.toFloat() * 44100 / 2048 * spectrumData[i]; tm += spectrumData[i] }; if (tm > 0) ws / tm else 0f } else 0f,
        spectralFlatness = spectralFlatness,
        spectralRolloff = spectralRolloff,
        spectralBandwidth = spectralBandwidth,
        harmonicToNoiseRatio = harmonicToNoiseRatio,
        thd = thd,
        waveformSine = waveformSine,
        waveformSaw = waveformSaw,
        waveformSquare = waveformSquare,
        waveformTriangle = waveformTriangle,
        waveformPulse = waveformPulse,
        oddEvenRatio = oddEvenRatio,
        harmonicDecay = harmonicDecay,
        brightness = brightness,
        warmth = warmth,
        octave = octaves
    )
}
