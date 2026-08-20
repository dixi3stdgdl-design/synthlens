package com.synthlens.app.engine

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.*

data class BatchResult(
    val uri: Uri,
    val fileName: String,
    val classification: MLClassificationResult?,
    val error: String? = null
)

class BatchAudioProcessor(private val context: Context, private val classifier: SynthMLClassifier) {

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private val _results = MutableStateFlow<List<BatchResult>>(emptyList())
    val results: StateFlow<List<BatchResult>> = _results

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    suspend fun processBatch(uris: List<Uri>) = withContext(Dispatchers.Default) {
        _isProcessing.value = true
        _progress.value = 0f
        val currentResults = mutableListOf<BatchResult>()
        _results.value = currentResults

        val total = uris.size

        for ((index, uri) in uris.withIndex()) {
            val fileName = getFileName(uri) ?: "Unknown_$index.wav"
            try {
                val pcmData = extractPcmFromWav(uri)
                if (pcmData != null) {
                    val analysis = analyzeOffline(pcmData)
                    val features = analysis.toSynthFeatures()
                    val result = classifier.classify(features)
                    currentResults.add(BatchResult(uri, fileName, result))
                } else {
                    currentResults.add(BatchResult(uri, fileName, null, "Not a valid WAV file or unsupported format"))
                }
            } catch (e: Exception) {
                currentResults.add(BatchResult(uri, fileName, null, e.message ?: "Unknown error"))
            }

            _results.value = currentResults.toList()
            _progress.value = (index + 1).toFloat() / total
        }

        _isProcessing.value = false
    }

    private fun getFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path?.let { path ->
                val cut = path.lastIndexOf('/')
                if (cut != -1) path.substring(cut + 1) else path
            }
        }
        return result
    }

    private fun extractPcmFromWav(uri: Uri): FloatArray? {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        inputStream.close()

        if (bytes.size < 44) return null // WAV header is at least 44 bytes
        
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        
        val riff = ByteArray(4)
        buffer.get(riff)
        if (String(riff) != "RIFF") return null
        
        buffer.position(8)
        val wave = ByteArray(4)
        buffer.get(wave)
        if (String(wave) != "WAVE") return null

        var fmtFound = false
        var channels = 1
        var sampleRate = 44100
        var bitsPerSample = 16

        buffer.position(12)
        while (buffer.remaining() >= 8) {
            val chunkId = ByteArray(4)
            buffer.get(chunkId)
            val chunkSize = buffer.getInt()

            if (String(chunkId) == "fmt ") {
                buffer.getShort()
                channels = buffer.getShort().toInt()
                sampleRate = buffer.getInt()
                buffer.getInt() 
                buffer.getShort() 
                bitsPerSample = buffer.getShort().toInt()
                fmtFound = true
                if (chunkSize > 16) buffer.position(buffer.position() + chunkSize - 16)
            } else if (String(chunkId) == "data") {
                if (!fmtFound) return null
                
                val numSamples = chunkSize / (bitsPerSample / 8) / channels
                val pcmFloat = FloatArray(numSamples)
                
                if (bitsPerSample == 16) {
                    for (i in 0 until numSamples) {
                        var sum = 0f
                        for (c in 0 until channels) {
                            sum += buffer.getShort().toFloat() / Short.MAX_VALUE
                        }
                        pcmFloat[i] = sum / channels 
                    }
                } else if (bitsPerSample == 24) {
                     for (i in 0 until numSamples) {
                        var sum = 0f
                        for (c in 0 until channels) {
                            val b1 = buffer.get().toInt() and 0xFF
                            val b2 = buffer.get().toInt() and 0xFF
                            val b3 = buffer.get().toInt()
                            val sample = (b3 shl 16) or (b2 shl 8) or b1
                            sum += sample.toFloat() / 8388607f
                        }
                        pcmFloat[i] = sum / channels
                    }
                } else if (bitsPerSample == 32) { 
                     for (i in 0 until numSamples) {
                        var sum = 0f
                        for (c in 0 until channels) {
                            sum += buffer.getFloat()
                        }
                        pcmFloat[i] = sum / channels
                    }
                } else {
                    return null 
                }
                return pcmFloat
            } else {
                buffer.position(buffer.position() + chunkSize)
            }
        }
        return null
    }

    private fun analyzeOffline(pcmData: FloatArray): AudioAnalysis {
        val read = pcmData.size
        if (read == 0) return AudioAnalysis()

        val normalized = normalizeGain(pcmData, read, 0.35f)
        val amplitude = calculateRMS(normalized, read)
        val peak = calculatePeak(normalized, read)
        
        val frameSize = 2048
        val frame = FloatArray(frameSize)
        if (read >= frameSize) {
            var maxEnergy = 0f
            var maxIdx = 0
            for (i in 0 until read - frameSize step 1024) {
                var energy = 0f
                for (j in 0 until frameSize) energy += normalized[i+j]*normalized[i+j]
                if (energy > maxEnergy) {
                    maxEnergy = energy
                    maxIdx = i
                }
            }
            System.arraycopy(normalized, maxIdx, frame, 0, frameSize)
        } else {
            System.arraycopy(normalized, 0, frame, 0, read)
        }

        val frequency = yinFrequencyDetection(frame, frameSize)
        val waveformType = classifyWaveform(frame, frameSize)
        val spectrum = calculateSpectrum(frame, frameSize)
        val harmonics = detectHarmonics(spectrum, frequency, 16)
        val thd = calculateTHD(harmonics, frequency)
        val hnr = calculateHarmonicToNoiseRatio(spectrum, frequency)
        val flatness = calculateSpectralFlatness(spectrum)
        val rolloff = calculateSpectralRolloff(spectrum)
        val bandwidth = calculateSpectralBandwidth(spectrum)

        return AudioAnalysis(
            frequency = frequency,
            amplitude = amplitude,
            waveformType = waveformType,
            octaves = frequencyToOctave(frequency),
            rmsLevel = 20 * log10(amplitude.coerceAtLeast(0.0001f)),
            peakLevel = 20 * log10(peak.coerceAtLeast(0.0001f)),
            thd = thd,
            spectrumData = spectrum,
            harmonics = harmonics,
            spectralFlatness = flatness,
            spectralRolloff = rolloff,
            spectralBandwidth = bandwidth,
            harmonicToNoiseRatio = hnr,
            harmonicCount = harmonics.size
        )
    }

    private fun calculateRMS(buffer: FloatArray, length: Int): Float {
        var sum = 0f
        for (i in 0 until length) sum += buffer[i] * buffer[i]
        return sqrt(sum / length)
    }

    private fun normalizeGain(buffer: FloatArray, length: Int, targetLevel: Float): FloatArray {
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

    private fun yinFrequencyDetection(buffer: FloatArray, length: Int): Float {
        if (length < 2) return 0f
        val SAMPLE_RATE = 44100
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
            } else 0f
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

    private fun calculateSpectrum(buffer: FloatArray, length: Int): FloatArray {
        val n = 1 shl (32 - Integer.numberOfLeadingZeros(length - 1).coerceAtLeast(1))
        val real = FloatArray(n)
        val imag = FloatArray(n)

        for (i in 0 until minOf(length, n)) {
            real[i] = buffer[i] * (0.54f - 0.46f * cos(2.0 * Math.PI * i / (n - 1)).toFloat())
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
            val angle = -Math.PI.toFloat() / halfStep
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

    private fun detectHarmonics(spectrum: FloatArray, fundamental: Float, maxHarmonics: Int = 16): List<Float> {
        val harmonics = mutableListOf<Float>()
        val SAMPLE_RATE = 44100
        val FFT_SIZE = 2048
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

    private fun calculateTHD(harmonics: List<Float>, fundamental: Float): Float {
        if (harmonics.size < 2 || harmonics[0] <= 0) return 0f
        var thdSum = 0f
        for (i in 1 until harmonics.size) thdSum += harmonics[i] * harmonics[i]
        return sqrt(thdSum) / harmonics[0]
    }

    private fun calculateHarmonicToNoiseRatio(spectrum: FloatArray, fundamental: Float): Float {
        if (fundamental <= 0 || spectrum.isEmpty()) return 0f
        var harmonicEnergy = 0f
        var noiseEnergy = 0f
        val SAMPLE_RATE = 44100
        val FFT_SIZE = 2048
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
        val SAMPLE_RATE = 44100
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
        val SAMPLE_RATE = 44100
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

    private fun frequencyToOctave(freq: Float): Int {
        if (freq <= 0 || !freq.isFinite()) return 0
        return (log2(freq / 16.35f)).toInt().coerceIn(0, 8)
    }
}
