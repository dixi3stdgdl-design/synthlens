package com.synthlens.app.engine

import android.Manifest
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

// ================================================================
//  DATA MODEL
// ================================================================



// ================================================================
//  EXTENSION: SynthInfo → DetectedSynthResult
//  Convierte una entrada del catálogo en resultado de detección
// ================================================================

fun SynthInfo.toDetectedSynthResult(confidence: Float): DetectedSynthResult {
    return DetectedSynthResult(
        name = name,
        brand = brand,
        category = category.name,
        confidence = confidence,
        frequencySignature = id,
        waveformType = synthesisType.name,
        filterType = filterProfile.type,
        oscillators = "${oscillatorProfile.count}x ${oscillatorProfile.types.joinToString("/")}",
        modulation = buildString {
            if (modulationProfile.hasModMatrix) append("Matrix(${modulationProfile.modMatrixSlots}) ")
            if (modulationProfile.hasArpeggiator) append("Arp ")
            if (modulationProfile.hasSequencer) append("Seq(${modulationProfile.sequencerSteps}) ")
            if (modulationProfile.hasAftertouch) append("AT ")
            if (modulationProfile.hasMPE) append("MPE ")
        }.ifEmpty { "LFO" },
        daw = "Hardware",
        effects = if (purchaseInfo.officialUrl.isNotEmpty())
            "Info: ${purchaseInfo.officialUrl}" else "",
        pattern = category.name.replace("_", " ")
    )
}

fun DetectedSynthResult.toStemSynthProfile(): StemSynthProfile {
    return StemSynthProfile(
        stemName = this.name,
        detectedSynth = this.name,
        brand = this.brand,
        category = this.category.replace("_", " "),
        confidence = this.confidence,
        energy = this.confidence,
        characteristics = mapOf(
            "engine" to this.modulation,
            "effects" to this.effects
        )
    )
}

// ================================================================
//  AUDIO ENGINE
// ================================================================

class AndroidAudioEngine(
    private val context: Context,
    private val scopeConfig: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) : AudioEngine {

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private val scope = scopeConfig
    private val stemSeparator = StemSeparator()
    private val stemAnalyzer = StemAnalyzer()
    private val synthDetector = SynthLensDetector(context)


    private val _analysis = MutableStateFlow(AudioAnalysis())
    override val analysis: StateFlow<AudioAnalysis> = _analysis.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    companion object {
        const val SAMPLE_RATE = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val FFT_SIZE = 2048
    }

    // ================================================================
    //  PERMISOS Y GRABACIÓN
    // ================================================================

    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun startRecording() {
        if (!hasPermission()) return
        if (_isRecording.value) return

        val bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
        if (bufferSize == AudioRecord.ERROR || bufferSize == AudioRecord.ERROR_BAD_VALUE) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize * 2
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                audioRecord?.release()
                audioRecord = null
                return
            }

            _isRecording.value = true
            audioRecord?.startRecording()

            recordingJob = scope.launch {
                val buffer = ShortArray(FFT_SIZE)
                val waveformBuffer = FloatArray(FFT_SIZE)
                var frameCount = 0
                var prevSpectrum = FloatArray(FFT_SIZE / 2)
                var currentPhase = 1 // 1: Song Detection, 2: Synth Analysis
                var currentSong: String? = null
                var currentArtist: String? = null
                var fingerprintBuffer = mutableListOf<Float>()
                var isFingerprinting = false

                while (isActive && _isRecording.value) {
                    val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (read > 0) {
                        frameCount++

                        for (i in 0 until read) {
                            waveformBuffer[i % FFT_SIZE] = buffer[i].toFloat() / Short.MAX_VALUE
                        }

                        val processed = removeDCOffset(waveformBuffer, read)
                        val gated = applyNoiseGate(processed, read, 0.008f)
                        val normalized = normalizeGain(gated, read, 0.35f)

                        val amplitude = calculateRMS(normalized, read)
                        val peak = calculatePeak(normalized, read)
                        val frequency = yinFrequencyDetection(normalized, read)
                        val waveformType = classifyWaveform(normalized, read)
                        val spectrum = calculateSpectrum(normalized, read)
                        val harmonics = detectHarmonics(spectrum, frequency, 16)
                        val thd = calculateTHD(harmonics, frequency)
                        val hnr = calculateHarmonicToNoiseRatio(spectrum, frequency)
                        val flatness = calculateSpectralFlatness(spectrum)
                        val rolloff = calculateSpectralRolloff(spectrum)
                        val bandwidth = calculateSpectralBandwidth(spectrum)
                        val noteName = frequencyToNoteName(frequency)

                        @Suppress("unused")
                        val flux = calculateSpectralFlux(prevSpectrum, spectrum)
                        prevSpectrum = spectrum.copyOf()

                        val waveformPoints = normalized.take(read).toList()

                        // ── Lógica de Transición de Fases (Fingerprint API) ──
                        if (currentPhase == 1 && !isFingerprinting) {
                            fingerprintBuffer.addAll(normalized.take(read))
                            // Aprox 3 segundos a 44100Hz = ~132300 samples
                            if (fingerprintBuffer.size >= 132300) {
                                isFingerprinting = true
                                val audioData = fingerprintBuffer.toFloatArray()
                                scope.launch {
                                    val result = NetworkClient.recognizeAudio(audioData, SAMPLE_RATE)
                                    if (result != null) {
                                        currentSong = result.first
                                        currentArtist = result.second
                                    } else {
                                        currentSong = "No Match / Error"
                                        currentArtist = "API Error"
                                    }
                                    currentPhase = 2
                                    fingerprintBuffer.clear()
                                }
                            }
                        }

                        // ── Detección cada 4 frames ──
                        if (frameCount % 4 == 0) {
                            try {
                                val hasRealSignal = amplitude > 0.08f
                                        && harmonics.isNotEmpty()
                                        && harmonics[0] > 0.001f
                                        && hasTonalContent(spectrum, frequency)

                                val detected = if (currentPhase == 2 && hasRealSignal) {
                                    val detectionResult = synthDetector.detect(normalized)
                                    val topMatch = detectionResult.topMatch
                                    if (topMatch != null && topMatch.conf > 0.4f) {
                                        val catalogMatch = SynthCatalogDB.searchByName(topMatch.name).firstOrNull()
                                        if (catalogMatch != null) {
                                            catalogMatch.toDetectedSynthResult(topMatch.conf)
                                        } else {
                                            DetectedSynthResult(
                                                name = topMatch.name,
                                                brand = topMatch.brand,
                                                category = topMatch.cat,
                                                confidence = topMatch.conf,
                                                frequencySignature = "${detectionResult.method}_${detectionResult.level}",
                                                waveformType = waveformType,
                                                filterType = "",
                                                oscillators = "",
                                                modulation = "",
                                                daw = "",
                                                effects = "",
                                                pattern = ""
                                            )
                                        }
                                    } else null
                                } else null

                                // Stems
                                val stemAnalysis =
                                    if (amplitude > 0.06f && hasRealSignal && frameCount % 8 == 0) {
                                        try {
                                            stemSeparator.processFloatBuffer(normalized, read)
                                        } catch (_: Exception) { null }
                                    } else null

                                val stemProfiles = stemAnalysis?.stems?.mapIndexed { index, stem ->
                                    stemAnalyzer.analyzeStem(stem, spectrum)
                                } ?: emptyList()

                                val dominantStem = stemAnalysis?.dominantStem?.name



                                // ── FIX: cierre correcto de AudioAnalysis ──
                                _analysis.value = AudioAnalysis(
                                    frequency = frequency,
                                    amplitude = amplitude,
                                    waveformType = waveformType,
                                    octaves = frequencyToOctave(frequency),
                                    rmsLevel = 20 * log10(amplitude.coerceAtLeast(0.0001f)),
                                    peakLevel = 20 * log10(peak.coerceAtLeast(0.0001f)),
                                    thd = thd,
                                    spectrumData = spectrum,
                                    waveformPoints = waveformPoints,
                                    harmonics = harmonics,
                                    isDetecting = true,
                                    detectionPhase = currentPhase,
                                    detectedSong = currentSong,
                                    detectedArtist = currentArtist,
                                    detectedSynth = detected,

                                    stemAnalysis = StemAnalysis(
                                        stems = stemProfiles,
                                        separationConfidence = stemAnalysis?.separationConfidence ?: 0f
                                    ),
                                    stemProfiles = stemProfiles,
                                    dominantStemName = dominantStem,
                                    spectralFlatness = flatness,
                                    spectralRolloff = rolloff,
                                    spectralBandwidth = bandwidth,
                                    harmonicToNoiseRatio = hnr,
                                    noteName = noteName,
                                    harmonicCount = harmonics.size
                                )
                            } catch (_: Exception) {}
                        }

                        delay(16)
                    }
                }
            }
        } catch (_: SecurityException) {
            _isRecording.value = false
        }
    }

    override fun stopRecording() {
        _isRecording.value = false
        recordingJob?.cancel()
        recordingJob = null
        try {
            audioRecord?.stop()
            audioRecord?.release()
        } catch (_: Exception) {}
        audioRecord = null
        _analysis.value = AudioAnalysis()
        // FIX: eliminados oscillatorDetector.reset() y mlAudioBuffer.clear()
        // que no existían en esta clase
    }

    override fun toggleRecording() {
        if (_isRecording.value) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    override fun destroy() {
        stopRecording()
        synthDetector.close()
    }

    // ================================================================
    //  PROCESAMIENTO DE SEÑAL
    // ================================================================

    private fun calculateRMS(buffer: FloatArray, length: Int): Float {
        var sum = 0f
        for (i in 0 until length) sum += buffer[i] * buffer[i]
        return sqrt(sum / length)
    }

    private fun removeDCOffset(buffer: FloatArray, length: Int): FloatArray {
        var sum = 0f
        for (i in 0 until length) sum += buffer[i]
        val dcOffset = sum / length
        val result = FloatArray(length)
        for (i in 0 until length) result[i] = buffer[i] - dcOffset
        return result
    }

    private fun applyNoiseGate(
        buffer: FloatArray, length: Int, threshold: Float = 0.01f
    ): FloatArray {
        val result = FloatArray(length)
        for (i in 0 until length) {
            result[i] = if (abs(buffer[i]) > threshold) buffer[i] else 0f
        }
        return result
    }

    private fun normalizeGain(
        buffer: FloatArray, length: Int, targetLevel: Float = 0.3f
    ): FloatArray {
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

    private fun calculatePeak(buffer: FloatArray, length: Int): Float {
        var peak = 0f
        for (i in 0 until length) {
            val a = abs(buffer[i])
            if (a > peak) peak = a
        }
        return peak
    }

    // ================================================================
    //  DETECCIÓN DE FRECUENCIA (YIN)
    // ================================================================

    private fun yinFrequencyDetection(buffer: FloatArray, length: Int): Float {
        if (length < 2) return 0f
        val halfLen = length / 2
        val tauMax = minOf(halfLen, SAMPLE_RATE / 30)
        val diff = FloatArray(tauMax)
        val cumSum = FloatArray(tauMax)

        for (tau in 1 until tauMax) {
            var sum = 0f
            for (j in 0 until halfLen) {
                val delta = buffer[j] - buffer[j + tau]
                sum += delta * delta
            }
            diff[tau] = sum
        }

        cumSum[0] = diff[1]
        for (tau in 2 until tauMax) {
            cumSum[tau - 1] = cumSum[tau - 2] + diff[tau]
        }

        var tauCandidate = -1
        for (tau in 1 until tauMax - 1) {
            if (diff[tau] < 0) continue
            if (cumSum[tau - 1] > 0 && diff[tau] / cumSum[tau - 1] < 0.2f) {
                tauCandidate = tau
                break
            }
        }

        if (tauCandidate < 0) {
            var bestCorr = 0f
            var bestTau = 0
            val minLag = SAMPLE_RATE / 1000
            val maxLag = SAMPLE_RATE / 30
            for (tau in minLag..minOf(maxLag, length / 2)) {
                var corr = 0f
                var energy = 0f
                val count = length - tau
                for (i in 0 until count) {
                    corr += buffer[i] * buffer[i + tau]
                    energy += buffer[i] * buffer[i]
                }
                if (energy > 0) corr /= sqrt(energy * count.toFloat())
                if (corr > bestCorr) {
                    bestCorr = corr
                    bestTau = tau
                }
            }
            return if (bestCorr > 0.3f && bestTau > 0) {
                SAMPLE_RATE.toFloat() / bestTau
            } else {
                estimateFrequencyFromZeroCrossings(buffer, length)
            }
        }

        val tau = tauCandidate
        val s0 = diff[tau]
        val s1 = if (tau + 1 < tauMax) diff[tau + 1] else s0
        val s2 = if (tau - 1 >= 0) diff[tau - 1] else s0
        val shift = if (s0 != s1 && s0 != s2) {
            (s1 - s2) / (2f * (s1 - 2f * s0 + s2))
        } else 0f

        return SAMPLE_RATE.toFloat() / (tau + shift)
    }

    private fun estimateFrequencyFromZeroCrossings(buffer: FloatArray, length: Int): Float {
        var crossings = 0
        for (i in 1 until length) {
            if ((buffer[i] >= 0 && buffer[i - 1] < 0) ||
                (buffer[i] < 0 && buffer[i - 1] >= 0)
            ) crossings++
        }
        return (crossings.toFloat() * SAMPLE_RATE) / (2f * length)
    }

    // ================================================================
    //  UTILIDADES DE AUDIO
    // ================================================================

    private fun frequencyToNoteName(freq: Float): String {
        if (freq <= 0 || !freq.isFinite()) return "---"
        val noteNames = arrayOf(
            "C", "C#", "D", "D#", "E", "F",
            "F#", "G", "G#", "A", "A#", "B"
        )
        val midiNote = (69 + 12 * log2(freq / 440f)).roundToInt().coerceIn(0, 127)
        val note = noteNames[midiNote % 12]
        val octave = (midiNote / 12) - 1
        val expectedFreq = 440f * 2f.pow((midiNote - 69) / 12f)
        val cents = if (expectedFreq > 0)
            (1200 * log2(freq / expectedFreq)).roundToInt() else 0
        return "$note$octave ${if (cents >= 0) "+" else ""}${cents}c"
    }

    private fun frequencyToOctave(freq: Float): Int {
        if (freq <= 0 || !freq.isFinite()) return 0
        return (log2(freq / 16.35f)).toInt().coerceIn(0, 8)
    }

    private fun classifyWaveform(buffer: FloatArray, length: Int): String {
        var sum = 0f
        var sumSq = 0f
        var peakVal = 0f

        for (i in 0 until length) {
            val v = abs(buffer[i])
            sum += v
            sumSq += v * v
            if (v > peakVal) peakVal = v
        }

        val mean = sum / length
        val rms = sqrt(sumSq / length)
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

    // ================================================================
    //  FFT Y ESPECTRO
    // ================================================================

    private fun calculateSpectrum(buffer: FloatArray, length: Int): FloatArray {
        val n = 1 shl (32 - Integer.numberOfLeadingZeros(length - 1).coerceAtLeast(1))
        val real = FloatArray(n)
        val imag = FloatArray(n)

        for (i in 0 until minOf(length, n)) {
            real[i] = buffer[i] * (0.54f - 0.46f *
                    cos(2.0 * PI * i / (n - 1)).toFloat())
        }

        fft(real, imag, n)

        val spectrumSize = n / 2
        val spectrum = FloatArray(spectrumSize)
        for (k in 0 until spectrumSize) {
            spectrum[k] = sqrt(real[k] * real[k] + imag[k] * imag[k])
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
                    val tReal = curWReal * real[i + k + halfStep] -
                            curWImag * imag[i + k + halfStep]
                    val tImag = curWReal * imag[i + k + halfStep] +
                            curWImag * real[i + k + halfStep]
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

    // ================================================================
    //  ANÁLISIS ESPECTRAL
    // ================================================================

    private fun detectHarmonics(
        spectrum: FloatArray, fundamental: Float, maxHarmonics: Int = 16
    ): List<Float> {
        val harmonics = mutableListOf<Float>()
        if (fundamental <= 0 || spectrum.isEmpty()) return harmonics

        for (h in 1..maxHarmonics) {
            val targetBin = ((h * fundamental * FFT_SIZE) / SAMPLE_RATE).toInt()
            if (targetBin >= spectrum.size) break
            val start = maxOf(0, targetBin - 3)
            val end = minOf(spectrum.size - 1, targetBin + 3)
            var maxVal = 0f
            for (i in start..end) {
                if (spectrum[i] > maxVal) maxVal = spectrum[i]
            }
            harmonics.add(maxVal)
        }
        return harmonics
    }

    private fun calculateHarmonicToNoiseRatio(
        spectrum: FloatArray, fundamental: Float
    ): Float {
        if (fundamental <= 0 || spectrum.isEmpty()) return 0f
        var harmonicEnergy = 0f
        var noiseEnergy = 0f
        val binWidth = SAMPLE_RATE.toFloat() / FFT_SIZE

        val harmonicBins = BooleanArray(spectrum.size)
        for (h in 1..8) {
            val centerBin = ((h * fundamental) / binWidth).toInt()
            if (centerBin >= spectrum.size) break
            val start = maxOf(0, centerBin - 2)
            val end = minOf(spectrum.size - 1, centerBin + 2)
            for (i in start..end) {
                harmonicBins[i] = true
                harmonicEnergy += spectrum[i] * spectrum[i]
            }
        }

        for (i in spectrum.indices) {
            if (!harmonicBins[i]) noiseEnergy += spectrum[i] * spectrum[i]
        }

        return if (noiseEnergy > 0) 10f * log10(harmonicEnergy / noiseEnergy) else 0f
    }

    private fun calculateSpectralFlatness(spectrum: FloatArray): Float {
        if (spectrum.isEmpty()) return 0f
        var logSum = 0f
        var linearSum = 0f
        var count = 0
        for (s in spectrum) {
            if (s > 0) {
                logSum += ln(s)
                linearSum += s
                count++
            }
        }
        if (count == 0 || linearSum == 0f) return 0f
        val geometricMean = exp(logSum / count)
        val arithmeticMean = linearSum / count
        return (geometricMean / arithmeticMean).coerceIn(0f, 1f)
    }

    private fun calculateSpectralRolloff(spectrum: FloatArray): Float {
        if (spectrum.isEmpty()) return 0f
        var totalEnergy = 0f
        for (s in spectrum) totalEnergy += s * s
        val threshold = totalEnergy * 0.85f
        var cumEnergy = 0f
        for (i in spectrum.indices) {
            cumEnergy += spectrum[i] * spectrum[i]
            if (cumEnergy >= threshold) {
                return (i.toFloat() / spectrum.size) * SAMPLE_RATE / 2
            }
        }
        return SAMPLE_RATE.toFloat() / 2
    }

    private fun calculateSpectralBandwidth(spectrum: FloatArray): Float {
        if (spectrum.isEmpty()) return 0f
        var weightedSum = 0f
        var totalMag = 0f
        for (i in spectrum.indices) {
            val freq = (i.toFloat() / spectrum.size) * SAMPLE_RATE / 2
            weightedSum += freq * spectrum[i]
            totalMag += spectrum[i]
        }
        val centroid = if (totalMag > 0) weightedSum / totalMag else 0f
        var weightedVariance = 0f
        for (i in spectrum.indices) {
            val freq = (i.toFloat() / spectrum.size) * SAMPLE_RATE / 2
            val d = freq - centroid
            weightedVariance += d * d * spectrum[i]
        }
        return if (totalMag > 0) sqrt(weightedVariance / totalMag) else 0f
    }

    private fun calculateSpectralFlux(
        prevSpectrum: FloatArray, currentSpectrum: FloatArray
    ): Float {
        val size = minOf(prevSpectrum.size, currentSpectrum.size)
        if (size == 0) return 0f
        var flux = 0f
        for (i in 0 until size) {
            val diff = currentSpectrum[i] - prevSpectrum[i]
            if (diff > 0) flux += diff
        }
        return flux / size
    }

    private fun calculateTHD(harmonics: List<Float>, @Suppress("UNUSED_PARAMETER") fundamental: Float): Float {
        if (harmonics.size < 2 || harmonics[0] <= 0) return 0f
        var thdSum = 0f
        for (i in 1 until harmonics.size) thdSum += harmonics[i] * harmonics[i]
        return sqrt(thdSum) / harmonics[0]
    }

    private fun hasTonalContent(spectrum: FloatArray, frequency: Float): Boolean {
        if (spectrum.isEmpty() || frequency <= 0) return false
        val binWidth = SAMPLE_RATE.toFloat() / spectrum.size
        val targetBin = (frequency / binWidth).toInt().coerceIn(0, spectrum.size - 1)
        val searchRange = 5
        var peakEnergy = 0f
        var totalEnergy = 0f
        for (i in spectrum.indices) {
            totalEnergy += spectrum[i] * spectrum[i]
            if (i in maxOf(0, targetBin - searchRange)..
                minOf(spectrum.size - 1, targetBin + searchRange)
            ) {
                if (spectrum[i] > peakEnergy) peakEnergy = spectrum[i]
            }
        }
        if (totalEnergy <= 0) return false
        val ratio = (peakEnergy * peakEnergy * (searchRange * 2 + 1)) / totalEnergy
        return ratio > 0.15f
    }

    // ================================================================
    //  DETECCIÓN DE SINTETIZADOR — NÚCLEO PROFESIONAL
    //  Usa SynthCatalogDB como fuente de verdad
    // ================================================================

    private data class HarmonicProfile(
        val oddEvenRatio: Float,
        val harmonicDecay: Float,
        val brightness: Float,
        val warmth: Float
    )

    private fun analyzeHarmonicProfile(harmonics: List<Float>): HarmonicProfile {
        if (harmonics.isEmpty()) return HarmonicProfile(0.5f, 0.5f, 0.5f, 0.5f)

        var oddSum = 0f
        var evenSum = 0f
        for (i in harmonics.indices) {
            if (i % 2 == 0) oddSum += harmonics[i] else evenSum += harmonics[i]
        }

        val oddEvenRatio = if (oddSum + evenSum > 0) oddSum / (oddSum + evenSum) else 0.5f
        val harmonicDecay = if (harmonics.size >= 2 && harmonics[0] > 0)
            harmonics.last() / harmonics[0] else 0.5f
        val highSum = harmonics.drop(4).sum()
        val totalSum = harmonics.sum()
        val brightness = if (totalSum > 0) highSum / totalSum else 0.3f
        val warmth = 1f - brightness

        return HarmonicProfile(oddEvenRatio, harmonicDecay, brightness, warmth)
    }

    private fun calculateSpectralCentroid(): Float {
        val spectrum = _analysis.value.spectrumData
        if (spectrum.isEmpty()) return 0f
        var weightedSum = 0f
        var totalMag = 0f
        for (i in spectrum.indices) {
            val freq = (i.toFloat() / spectrum.size) * SAMPLE_RATE / 2
            weightedSum += freq * spectrum[i]
            totalMag += spectrum[i]
        }
        return if (totalMag > 0) weightedSum / totalMag else 0f
    }

    private fun calculateWaveformScore(waveformType: String): Float {
        return when (waveformType) {
            "Saw" -> 0.85f
            "Square" -> 0.80f
            "Triangle" -> 0.75f
            "Sine" -> 0.70f
            "Pulse" -> 0.65f
            else -> 0.50f
        }
    }

    private fun calculateConfidence(
        frequency: Float,
        waveformType: String,
        harmonics: List<Float>,
        amplitude: Float,
        harmonicProfile: HarmonicProfile,
        spectralCentroid: Float,
        waveformScore: Float,
        spectralFlatness: Float = 0f,
        spectralRolloff: Float = 0f
    ): Float {
        val freqScore = when {
            frequency in 80f..400f -> 0.9f
            frequency in 40f..80f || frequency in 400f..1000f -> 0.7f
            frequency in 20f..40f || frequency in 1000f..4000f -> 0.5f
            else -> 0.3f
        }

        val harmonicsScore = if (harmonics.isNotEmpty()) {
            (harmonics.sum() / harmonics.size).coerceIn(0f, 1f)
        } else 0.3f

        val amplitudeScore = (amplitude * 2.5f).coerceIn(0f, 1f)

        val spectralScore = when (spectralCentroid) {
            in 200f..2000f -> 0.8f
            in 100f..4000f -> 0.6f
            else -> 0.4f
        }

        val flatnessBonus = if (spectralFlatness < 0.3f) 0.1f else 0f
        val rolloffBonus = if (spectralRolloff in 500f..8000f) 0.05f else 0f

        return freqScore * 0.20f +
                waveformScore * 0.18f +
                harmonicsScore * 0.22f +
                amplitudeScore * 0.15f +
                spectralScore * 0.15f +
                flatnessBonus +
                rolloffBonus
    }

    // ── Normaliza keys del scoring → IDs del catálogo ──
    private fun normalizeSignatureId(key: String): String {
        return when (key) {
            "korg_miniloguexd" -> "korg_minilogue_xd"
            "behringer_modeld" -> "behringer_model_d"
            "elektron_analogfour" -> "elektron_analog_keys"
            else -> key
        }
    }

    /**
     * Punto de entrada de la detección heurística.
     * Busca coincidencia con scoring → catálogo → fallback inline.
     */
    private fun analyzeSynthSignature(
        frequency: Float,
        waveformType: String,
        harmonics: List<Float>,
        amplitude: Float,
        spectralFlatness: Float = 0f,
        spectralRolloff: Float = 0f,
        spectralBandwidth: Float = 0f
    ): DetectedSynthResult? {
        if (frequency < 30f || frequency > 18000f || amplitude < 0.05f) return null
        if (harmonics.size < 3 || harmonics[0] < 0.001f) return null

        val harmonicEnergy = harmonics.sum()
        if (harmonicEnergy < 0.01f) return null

        val harmonicProfile = analyzeHarmonicProfile(harmonics)
        val spectralCentroid = calculateSpectralCentroid()
        val waveformScore = calculateWaveformScore(waveformType)

        val baseConfidence = calculateConfidence(
            frequency, waveformType, harmonics, amplitude,
            harmonicProfile, spectralCentroid, waveformScore,
            spectralFlatness, spectralRolloff
        )

        if (baseConfidence < 0.40f) return null

        val signature = matchSynthSignature(
            frequency, waveformType, harmonics, amplitude,
            harmonicProfile, spectralCentroid, baseConfidence,
            spectralFlatness, spectralRolloff
        )

        if (signature == "unknown") return null

        // ── CATÁLOGO PRIMERO ──
        val catalogId = normalizeSignatureId(signature)
        val catalogEntry = SynthCatalogDB.getSynthById(catalogId)
        if (catalogEntry != null) {
            return catalogEntry.toDetectedSynthResult(baseConfidence)
        }

        // ── FALLBACK: base de datos inline ──
        return synthsDatabase[signature]?.copy(confidence = baseConfidence)
    }

    private fun matchSynthSignature(
        frequency: Float,
        waveformType: String,
        harmonics: List<Float>,
        amplitude: Float,
        harmonicProfile: HarmonicProfile,
        spectralCentroid: Float,
        baseConfidence: Float,
        spectralFlatness: Float = 0f,
        spectralRolloff: Float = 0f
    ): String {
        val scores = mutableMapOf<String, Float>()

        scores["moog_grandmother"] = scoreMoogGrandmother(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["moog_sub37"] = scoreMoogSub37(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_ms20"] = scoreKorgMS20(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_miniloguexd"] = scoreKorgMinilogueXD(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["roland_juno106"] = scoreRolandJuno106(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["roland_tb303"] = scoreRolandTB303(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["sequential_prophet6"] = scoreSequentialProphet6(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["novation_peak"] = scoreNovationPeak(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["arturia_matrixbrute"] = scoreArturiaMatrixBrute(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["moog_model_d"] = scoreMoogModelD(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["sequential_prophet5"] = scoreProphet5(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["sequential_pro3"] = scorePro3(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["roland_sh101"] = scoreSH101(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["roland_jupiter8"] = scoreJupiter8(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_monologue"] = scoreKorgMonologue(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["novation_bassstation2"] = scoreBassStationII(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["arturia_microfreak"] = scoreMicroFreak(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["moog_one"] = scoreMoogOne(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["behringer_td3"] = scoreBehringerTD3(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["moog_matriarch"] = scoreMoogMatriarch(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["moog_subsequent25"] = scoreMoogSubsequent25(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_prologue"] = scoreKorgPrologue(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_wavestate"] = scoreKorgWavestate(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["korg_opsix"] = scoreKorgOpsix(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["arturia_minibrute2s"] = scoreArturiaMiniBrute2S(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["arturia_polybrute"] = scoreArturiaPolyBrute(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["novation_summit"] = scoreNovationSummit(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["roland_system8"] = scoreRolandSystem8(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["sequential_obx8"] = scoreSequentialOBX8(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["behringer_deepmind12"] = scoreBehringerDeepMind12(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["behringer_modeld"] = scoreBehringerModelD(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["elektron_analogfour"] = scoreElektronAnalogFour(frequency, waveformType, harmonicProfile, spectralCentroid)
        scores["elektron_digitone"] = scoreElektronDigitone(frequency, waveformType, harmonicProfile, spectralCentroid)

        // ── BONUS: score contra specs del catálogo ──
        for ((key, _) in scores) {
            val catalogId = normalizeSignatureId(key)
            val catBonus = scoreAgainstCatalogSpecs(
                catalogId, waveformType, harmonicProfile, spectralCentroid
            )
            scores[key] = (scores[key] ?: 0f) + catBonus
        }

        val bestMatch = scores.maxByOrNull { it.value }
        val bestScore = bestMatch?.value ?: 0f

        return if (bestScore > 0.55f) bestMatch?.key ?: "unknown" else "unknown"
    }

    /**
     * Bonus profesional: compara la señal detectada contra las specs
     * reales del sintetizador en SynthCatalogDB.
     */
    private fun scoreAgainstCatalogSpecs(
        catalogId: String,
        waveformType: String,
        harmonicProfile: HarmonicProfile,
        spectralCentroid: Float
    ): Float {
        val synth = SynthCatalogDB.getSynthById(catalogId) ?: return 0f
        var bonus = 0f

        // Oscillator types del catálogo coinciden con waveform detectada
        val catalogOscTypes = synth.oscillatorProfile.types.map { it.lowercase() }
        val detectedLower = waveformType.lowercase()
        if (catalogOscTypes.any { it.contains(detectedLower) || detectedLower.contains(it) }) {
            bonus += 0.05f
        }

        // Sub-oscillator → warmness esperada
        if (synth.oscillatorProfile.hasSubOscillator && harmonicProfile.warmth > 0.6f) {
            bonus += 0.03f
        }

        // Ring mod → brightness alta
        if (synth.oscillatorProfile.hasRingMod && harmonicProfile.brightness > 0.5f) {
            bonus += 0.02f
        }

        // Filtro self-oscillante → spectral centroid alto
        if (synth.filterProfile.hasSelfOscillation && spectralCentroid > 2000f) {
            bonus += 0.02f
        }

        return bonus
    }

    // ================================================================
    //  FUNCIONES DE SCORING (34 sintetizadores)
    // ================================================================

    private fun scoreMoogGrandmother(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw") s += 0.3f
        if (f in 50f..500f) s += 0.25f
        if (hp.warmth > 0.6f) s += 0.2f
        if (hp.harmonicDecay < 0.4f) s += 0.15f
        if (sc in 300f..1500f) s += 0.1f
        return s
    }

    private fun scoreMoogSub37(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.25f
        if (f in 40f..400f) s += 0.25f
        if (hp.brightness in 0.3f..0.6f) s += 0.2f
        if (sc in 200f..1200f) s += 0.15f
        if (hp.oddEvenRatio in 0.4f..0.7f) s += 0.15f
        return s
    }

    private fun scoreKorgMS20(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Square") s += 0.3f
        if (f in 30f..300f) s += 0.25f
        if (hp.brightness > 0.5f) s += 0.2f
        if (sc in 400f..2000f) s += 0.15f
        if (hp.oddEvenRatio > 0.6f) s += 0.1f
        return s
    }

    private fun scoreKorgMinilogueXD(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.2f
        if (f in 100f..2000f) s += 0.25f
        if (hp.brightness in 0.4f..0.7f) s += 0.2f
        if (sc in 500f..2500f) s += 0.2f
        if (hp.harmonicDecay in 0.2f..0.5f) s += 0.15f
        return s
    }

    private fun scoreRolandJuno106(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw") s += 0.3f
        if (f in 100f..1000f) s += 0.2f
        if (hp.warmth > 0.5f) s += 0.2f
        if (sc in 300f..1200f) s += 0.15f
        if (hp.harmonicDecay < 0.5f) s += 0.15f
        return s
    }

    private fun scoreRolandTB303(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Pulse") s += 0.35f
        if (f in 40f..400f) s += 0.25f
        if (hp.brightness > 0.4f) s += 0.2f
        if (sc in 200f..800f) s += 0.1f
        if (hp.oddEvenRatio > 0.5f) s += 0.1f
        return s
    }

    private fun scoreSequentialProphet6(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw") s += 0.25f
        if (f in 80f..1500f) s += 0.25f
        if (hp.warmth > 0.55f) s += 0.2f
        if (sc in 400f..1500f) s += 0.15f
        if (hp.harmonicDecay in 0.25f..0.45f) s += 0.15f
        return s
    }

    private fun scoreNovationPeak(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Triangle" || w == "Saw") s += 0.2f
        if (f in 200f..3000f) s += 0.25f
        if (hp.brightness in 0.3f..0.6f) s += 0.2f
        if (sc in 600f..2500f) s += 0.2f
        if (hp.oddEvenRatio in 0.45f..0.65f) s += 0.15f
        return s
    }

    private fun scoreArturiaMatrixBrute(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Square" || w == "Saw") s += 0.25f
        if (f in 50f..1000f) s += 0.25f
        if (hp.brightness > 0.45f) s += 0.2f
        if (sc in 300f..1800f) s += 0.15f
        if (hp.oddEvenRatio in 0.4f..0.7f) s += 0.15f
        return s
    }

    private fun scoreMoogModelD(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 30f..500f) s += 0.25f
        if (hp.warmth > 0.7f) s += 0.25f
        if (hp.harmonicDecay < 0.35f) s += 0.1f
        if (sc in 200f..1000f) s += 0.1f
        return s
    }

    private fun scoreProphet5(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Pulse") s += 0.3f
        if (f in 80f..1200f) s += 0.25f
        if (hp.warmth > 0.6f) s += 0.2f
        if (sc in 300f..1200f) s += 0.15f
        if (hp.harmonicDecay in 0.2f..0.4f) s += 0.1f
        return s
    }

    private fun scorePro3(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.25f
        if (f in 40f..1500f) s += 0.2f
        if (hp.brightness in 0.3f..0.7f) s += 0.2f
        if (sc in 300f..2000f) s += 0.2f
        if (hp.oddEvenRatio in 0.35f..0.65f) s += 0.15f
        return s
    }

    private fun scoreSH101(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 50f..800f) s += 0.25f
        if (hp.brightness > 0.35f) s += 0.2f
        if (sc in 400f..1800f) s += 0.15f
        if (hp.harmonicDecay < 0.5f) s += 0.1f
        return s
    }

    private fun scoreJupiter8(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.2f
        if (f in 100f..3000f) s += 0.25f
        if (hp.brightness in 0.4f..0.7f) s += 0.2f
        if (sc in 500f..2500f) s += 0.2f
        if (hp.warmth > 0.4f) s += 0.15f
        return s
    }

    private fun scoreKorgMonologue(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 40f..600f) s += 0.25f
        if (hp.brightness > 0.4f) s += 0.2f
        if (sc in 300f..1500f) s += 0.15f
        if (hp.oddEvenRatio > 0.5f) s += 0.1f
        return s
    }

    private fun scoreBassStationII(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Pulse") s += 0.3f
        if (f in 40f..500f) s += 0.25f
        if (hp.brightness in 0.3f..0.6f) s += 0.2f
        if (sc in 300f..1200f) s += 0.15f
        if (hp.warmth > 0.4f) s += 0.1f
        return s
    }

    private fun scoreMicroFreak(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w != "Unknown") s += 0.15f
        if (f in 80f..4000f) s += 0.2f
        if (hp.brightness > 0.3f) s += 0.2f
        if (sc in 400f..4000f) s += 0.25f
        if (hp.oddEvenRatio in 0.3f..0.8f) s += 0.2f
        return s
    }

    private fun scoreMoogOne(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.25f
        if (f in 50f..2000f) s += 0.2f
        if (hp.warmth > 0.65f) s += 0.25f
        if (hp.harmonicDecay < 0.4f) s += 0.15f
        if (sc in 200f..1500f) s += 0.15f
        return s
    }

    private fun scoreBehringerTD3(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 40f..400f) s += 0.25f
        if (hp.brightness > 0.4f) s += 0.2f
        if (sc in 200f..800f) s += 0.1f
        if (hp.oddEvenRatio > 0.5f) s += 0.15f
        return s
    }

    private fun scoreMoogMatriarch(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Triangle" || w == "Square") s += 0.25f
        if (f in 30f..1000f) s += 0.2f
        if (hp.warmth > 0.6f) s += 0.25f
        if (hp.harmonicDecay < 0.4f) s += 0.15f
        if (sc in 200f..1200f) s += 0.15f
        return s
    }

    private fun scoreMoogSubsequent25(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 40f..500f) s += 0.25f
        if (hp.warmth > 0.5f) s += 0.2f
        if (sc in 200f..1000f) s += 0.15f
        if (hp.oddEvenRatio in 0.4f..0.7f) s += 0.1f
        return s
    }

    private fun scoreKorgPrologue(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.2f
        if (f in 100f..4000f) s += 0.25f
        if (hp.brightness in 0.3f..0.7f) s += 0.2f
        if (sc in 500f..3000f) s += 0.2f
        if (hp.harmonicDecay in 0.2f..0.6f) s += 0.15f
        return s
    }

    private fun scoreKorgWavestate(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w != "Unknown") s += 0.15f
        if (f in 80f..6000f) s += 0.2f
        if (hp.brightness > 0.4f) s += 0.2f
        if (sc in 400f..5000f) s += 0.25f
        if (hp.oddEvenRatio in 0.3f..0.8f) s += 0.2f
        return s
    }

    private fun scoreKorgOpsix(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Sine" || w == "Triangle") s += 0.25f
        if (f in 100f..5000f) s += 0.2f
        if (hp.brightness > 0.3f) s += 0.2f
        if (sc in 300f..4000f) s += 0.2f
        if (hp.oddEvenRatio in 0.2f..0.7f) s += 0.15f
        return s
    }

    private fun scoreArturiaMiniBrute2S(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 30f..800f) s += 0.25f
        if (hp.brightness > 0.4f) s += 0.2f
        if (sc in 300f..1500f) s += 0.15f
        if (hp.warmth > 0.4f) s += 0.1f
        return s
    }

    private fun scoreArturiaPolyBrute(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Triangle" || w == "Square") s += 0.2f
        if (f in 50f..3000f) s += 0.25f
        if (hp.warmth > 0.5f) s += 0.2f
        if (sc in 300f..2000f) s += 0.2f
        if (hp.harmonicDecay in 0.2f..0.5f) s += 0.15f
        return s
    }

    private fun scoreNovationSummit(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Triangle" || w == "Square") s += 0.2f
        if (f in 100f..5000f) s += 0.25f
        if (hp.brightness in 0.3f..0.65f) s += 0.2f
        if (sc in 500f..3000f) s += 0.2f
        if (hp.oddEvenRatio in 0.4f..0.7f) s += 0.15f
        return s
    }

    private fun scoreRolandSystem8(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.2f
        if (f in 50f..4000f) s += 0.25f
        if (hp.brightness in 0.3f..0.7f) s += 0.2f
        if (sc in 400f..3000f) s += 0.2f
        if (hp.warmth > 0.4f) s += 0.15f
        return s
    }

    private fun scoreSequentialOBX8(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 40f..1500f) s += 0.25f
        if (hp.warmth > 0.6f) s += 0.2f
        if (sc in 300f..1500f) s += 0.15f
        if (hp.harmonicDecay < 0.4f) s += 0.1f
        return s
    }

    private fun scoreBehringerDeepMind12(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.25f
        if (f in 60f..2000f) s += 0.25f
        if (hp.warmth > 0.5f) s += 0.2f
        if (sc in 400f..1800f) s += 0.15f
        if (hp.harmonicDecay in 0.2f..0.5f) s += 0.15f
        return s
    }

    private fun scoreBehringerModelD(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square") s += 0.3f
        if (f in 30f..500f) s += 0.25f
        if (hp.warmth > 0.7f) s += 0.25f
        if (hp.harmonicDecay < 0.35f) s += 0.1f
        if (sc in 200f..1000f) s += 0.1f
        return s
    }

    private fun scoreElektronAnalogFour(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Saw" || w == "Square" || w == "Triangle") s += 0.2f
        if (f in 40f..3000f) s += 0.25f
        if (hp.brightness in 0.3f..0.7f) s += 0.2f
        if (sc in 400f..2500f) s += 0.2f
        if (hp.oddEvenRatio in 0.35f..0.65f) s += 0.15f
        return s
    }

    private fun scoreElektronDigitone(f: Float, w: String, hp: HarmonicProfile, sc: Float): Float {
        var s = 0f
        if (w == "Sine" || w == "Triangle") s += 0.25f
        if (f in 100f..5000f) s += 0.2f
        if (hp.brightness > 0.3f) s += 0.2f
        if (sc in 300f..4000f) s += 0.2f
        if (hp.oddEvenRatio in 0.2f..0.7f) s += 0.15f
        return s
    }

    // ================================================================
    //  BASE DE DATOS INLINE (FALLBACK)
    //  Solo para sintetizadores que aún no están en SynthCatalogDB
    // ================================================================

    @Suppress("LongMethod")
    private val synthsDatabase = mapOf(
        "roland_tb303" to DetectedSynthResult(
            "TB-303", "Roland", "Monophonic Analog Bass", 0f,
            "roland_tb303", "", "Diode Ladder LP 18dB", "1 VCO",
            "Accent/Slide", "Unknown", "None", "Acid Pattern"
        ),
        "sequential_pro3" to DetectedSynthResult(
            "Pro-3", "Sequential", "Monophonic Analog", 0f,
            "sequential_pro3", "", "SSM2040/CEM3320/Steiner-Parker",
            "3 VCO + Sub + Noise", "Mod Matrix 16x4", "Unknown", "Overdrive", "Pattern Pro3"
        ),
        "roland_sh101" to DetectedSynthResult(
            "SH-101", "Roland", "Monophonic Analog", 0f,
            "roland_sh101", "", "IR3109 LP 24dB", "1 VCO + Sub + Noise",
            "LFO, Mod Grip", "Unknown", "None", "Techno Pattern"
        ),
        "roland_jupiter8" to DetectedSynthResult(
            "JUPITER-8", "Roland", "Polyphonic Analog", 0f,
            "roland_jupiter8", "", "IR3109 LP/HP 12dB/24dB", "2 VCO per voice",
            "Cross-modulation", "Unknown", "Chorus", "Pattern JP8"
        ),
        "korg_monologue" to DetectedSynthResult(
            "Monologue", "Korg", "Monophonic Analog", 0f,
            "korg_monologue", "", "Korg MS-20 style LP 12/24dB",
            "1 VCO + Multi-osc", "Sequencer, Microtuning", "Unknown", "None", "Pattern Mono"
        ),
        "novation_bassstation2" to DetectedSynthResult(
            "Bass Station II", "Novation", "Monophonic Analog", 0f,
            "novation_bassstation2", "", "Classic/Acid LP 12/24dB",
            "2 VCO + Sub + Noise", "Overdrive, Arp", "Unknown", "Overdrive", "Pattern BS2"
        ),
        "moog_one" to DetectedSynthResult(
            "Moog One", "Moog", "Polyphonic Analog", 0f,
            "moog_one", "", "Moog Ladder + State-Variable",
            "3 VCO per voice", "Tri-timbral, Mod Matrix", "Unknown",
            "Analog Effects", "Pattern Moog1"
        ),
        "behringer_td3" to DetectedSynthResult(
            "TD-3", "Behringer", "Monophonic Analog Bass", 0f,
            "behringer_td3", "", "Diode Ladder LP 18dB", "1 VCO",
            "Accent/Slide", "Unknown", "Distortion", "Acid Pattern"
        ),
        "moog_subsequent25" to DetectedSynthResult(
            "Subsequent 25", "Moog", "Paraphonic Analog", 0f,
            "moog_subsequent25", "", "Moog Ladder LP 24dB (Multidrive)",
            "2 VCO + Sub + Noise", "Multidrive", "Unknown", "Overdrive", "Pattern Sub25"
        ),
        "korg_prologue" to DetectedSynthResult(
            "Prologue", "Korg", "Polyphonic Analog/Hybrid", 0f,
            "korg_prologue", "", "2-pole LP/HP/BP",
            "2 Analog VCO + Multi-Engine", "User oscillators", "Unknown",
            "Effects program", "Pattern Prologue"
        ),
        "korg_wavestate" to DetectedSynthResult(
            "wavestate", "Korg", "Digital Wavesequencing", 0f,
            "korg_wavestate", "", "Digital multi-mode",
            "Wave Sequencing 2.0", "Modulations", "Unknown", "Effects", "Pattern Wavestate"
        ),
        "korg_opsix" to DetectedSynthResult(
            "opsix", "Korg", "Digital FM", 0f,
            "korg_opsix", "", "Digital multi-mode",
            "6-operator FM (14 algorithms)", "Operator ratios", "Unknown",
            "Effects", "Pattern FM"
        ),
        "roland_system8" to DetectedSynthResult(
            "SYSTEM-8", "Roland", "Polyphonic Digital Modeling", 0f,
            "roland_system8", "", "ACB Digital LP/HP/BP",
            "3 Osc (PLUG-OUT models)", "Cross-mod, LFO", "Unknown",
            "Effects", "Pattern System8"
        ),
        "sequential_obx8" to DetectedSynthResult(
            "OB-X8", "Sequential (Oberheim)", "Polyphonic Analog", 0f,
            "sequential_obx8", "", "SEM-style LP/HP/BP 12/24dB",
            "2 VCO per voice", "Cross-mod", "Unknown", "None", "Pattern OB"
        ),
        "generic_analog" to DetectedSynthResult(
            "Analog Synth", "Unknown", "Analog", 0f,
            "generic", "", "Low-Pass Filter", "VCO(s)",
            "LFO Modulation", "Unknown", "Unknown", "Pattern X"
        )
    )
}
