package com.synthlens.app.engine

import kotlin.math.*

class FeatureExtractor {

    companion object {
        const val SAMPLE_RATE = 44100
        const val FFT_SIZE = 2048
        const val HOP_LENGTH = 1024
        const val NUM_MFCC = 13
        const val NUM_MEL = 40
        const val FEATURE_COUNT = 65
    }

    private val melBank: Array<FloatArray> = buildMelBank()
    private val hannWindow: FloatArray = FloatArray(FFT_SIZE) {
        (0.5 * (1.0 - cos(2.0 * PI * it / (FFT_SIZE - 1)))).toFloat()
    }
    private var previousSpectrum: FloatArray? = null

    fun extractFeatures(samples: FloatArray): FloatArray {
        if (samples.size < FFT_SIZE) return FloatArray(FEATURE_COUNT)
        val windows = mutableListOf<FloatArray>()
        var idx = 0
        while (idx + FFT_SIZE <= samples.size) {
            val windowed = FloatArray(FFT_SIZE) { samples[idx + it] * hannWindow[it] }
            windows.add(windowed)
            idx += HOP_LENGTH
        }
        if (windows.isEmpty()) return FloatArray(FEATURE_COUNT)
        previousSpectrum = null
        val allFeatures = windows.map { extractOneWindow(it) }
        return averageFeatures(allFeatures)
    }

    private fun extractOneWindow(win: FloatArray): FloatArray {
        val spectrum = magnitudeSpectrum(win)
        val f = FloatArray(FEATURE_COUNT)
        var i = 0
        val mfcc = computeMFCCs(spectrum)
        for (j in 0 until NUM_MFCC) f[i++] = mfcc[j]
        val d1 = computeDelta(mfcc)
        for (j in 0 until NUM_MFCC) f[i++] = d1[j]
        val d2 = computeDelta(d1)
        for (j in 0 until NUM_MFCC) f[i++] = d2[j]
        f[i++] = computeCentroid(spectrum) / (SAMPLE_RATE / 2f)
        f[i++] = computeRolloff(spectrum) / (SAMPLE_RATE / 2f)
        f[i++] = computeZCR(win)
        f[i++] = computeHNR(win)
        f[i++] = computeFlux(spectrum)
        val chroma = computeChroma(spectrum)
        for (j in 0 until 12) f[i++] = chroma[j]
        val env = computeEnvelope(win)
        for (j in 0 until 5) f[i++] = env[j]
        val fc = computeFilterChars(spectrum)
        for (j in 0 until 4) f[i++] = fc[j]
        return f
    }

    private fun magnitudeSpectrum(win: FloatArray): FloatArray {
        val n = FFT_SIZE
        val re = FloatArray(n)
        val im = FloatArray(n)
        for (j in 0 until minOf(win.size, n)) re[j] = win[j]
        performFFT(re, im, n)
        return FloatArray(n / 2) { sqrt(re[it] * re[it] + im[it] * im[it]) }
    }

    private fun performFFT(re: FloatArray, im: FloatArray, n: Int) {
        var j = 0
        for (i in 0 until n - 1) {
            if (i < j) {
                var t = re[i]; re[i] = re[j]; re[j] = t
                t = im[i]; im[i] = im[j]; im[j] = t
            }
            var m = n shr 1
            while (m >= 1 && j >= m) { j -= m; m = m shr 1 }
            j += m
        }
        var step = 1
        while (step < n) {
            val halfStep = step
            step = step shl 1
            val angle = (-PI / halfStep).toFloat()
            val wR = cos(angle)
            val wI = sin(angle)
            var ii = 0
            while (ii < n) {
                var curR = 1f
                var curI = 0f
                for (k in 0 until halfStep) {
                    val tR = curR * re[ii + k + halfStep] - curI * im[ii + k + halfStep]
                    val tI = curR * im[ii + k + halfStep] + curI * re[ii + k + halfStep]
                    re[ii + k + halfStep] = re[ii + k] - tR
                    im[ii + k + halfStep] = im[ii + k] - tI
                    re[ii + k] += tR
                    im[ii + k] += tI
                    val newR = curR * wR - curI * wI
                    curI = curR * wI + curI * wR
                    curR = newR
                }
                ii += step
            }
        }
    }

    private fun computeMFCCs(spectrum: FloatArray): FloatArray {
        val melEnergies = FloatArray(NUM_MEL)
        for (m in 0 until NUM_MEL) {
            var energy = 0f
            val bankSize = melBank[m].size
            for (k in 0 until minOf(spectrum.size, bankSize)) {
                energy += spectrum[k] * spectrum[k] * melBank[m][k]
            }
            melEnergies[m] = ln(energy.coerceAtLeast(1e-10f))
        }
        return FloatArray(NUM_MFCC) { c ->
            var sum = 0f
            for (m in 0 until NUM_MEL) {
                sum += melEnergies[m] * cos(PI.toFloat() * c * (2f * m + 1f) / (2f * NUM_MEL))
            }
            sum
        }
    }

    private fun buildMelBank(): Array<FloatArray> {
        val numBins = FFT_SIZE / 2
        val melMin = 2595f * log10(1f)
        val melMax = 2595f * log10(1f + SAMPLE_RATE / 2f / 700f)
        val melPoints = FloatArray(NUM_MEL + 2) {
            700f * (10f.pow((melMin + it * (melMax - melMin) / (NUM_MEL + 1)) / 2595f) - 1f)
        }
        val binFreqs = FloatArray(numBins) { it.toFloat() * SAMPLE_RATE / FFT_SIZE }
        return Array(NUM_MEL) { m ->
            FloatArray(numBins) { k ->
                val freq = binFreqs[k]
                when {
                    freq in melPoints[m]..melPoints[m + 1] && melPoints[m + 1] > melPoints[m] ->
                        (freq - melPoints[m]) / (melPoints[m + 1] - melPoints[m])
                    freq in melPoints[m + 1]..melPoints[m + 2] && melPoints[m + 2] > melPoints[m + 1] ->
                        (melPoints[m + 2] - freq) / (melPoints[m + 2] - melPoints[m + 1])
                    else -> 0f
                }
            }
        }
    }

    private fun computeDelta(values: FloatArray): FloatArray {
        return FloatArray(values.size) { i ->
            val prev = if (i > 0) values[i - 1] else values[i]
            val next = if (i < values.size - 1) values[i + 1] else values[i]
            (next - prev) / 2f
        }
    }

    private fun computeCentroid(spectrum: FloatArray): Float {
        var weightedSum = 0f
        var totalMag = 0f
        for (i in spectrum.indices) {
            val freq = i.toFloat() * SAMPLE_RATE / FFT_SIZE
            weightedSum += freq * spectrum[i]
            totalMag += spectrum[i]
        }
        return if (totalMag > 0) weightedSum / totalMag else 0f
    }

    private fun computeRolloff(spectrum: FloatArray): Float {
        var totalEnergy = 0f
        for (v in spectrum) totalEnergy += v * v
        val threshold = totalEnergy * 0.85f
        var cumulative = 0f
        for (i in spectrum.indices) {
            cumulative += spectrum[i] * spectrum[i]
            if (cumulative >= threshold) return i.toFloat() * SAMPLE_RATE / FFT_SIZE
        }
        return SAMPLE_RATE / 2f
    }

    private fun computeZCR(samples: FloatArray): Float {
        var crossings = 0
        for (i in 1 until samples.size) {
            if ((samples[i - 1] >= 0 && samples[i] < 0) ||
                (samples[i - 1] < 0 && samples[i] >= 0)
            ) crossings++
        }
        return crossings.toFloat() / samples.size.coerceAtLeast(1)
    }

    private fun computeHNR(samples: FloatArray): Float {
        val n = samples.size
        if (n < 4) return 0f
        val energy = samples.sumOf { (it * it).toDouble() }.toFloat()
        if (energy < 1e-10f) return 0f
        val minLag = SAMPLE_RATE / 1000
        val maxLag = minOf(SAMPLE_RATE / 30, n / 2)
        var bestCorr = 0f
        for (lag in minLag..maxLag) {
            var corr = 0f
            for (i in 0 until n - lag) corr += samples[i] * samples[i + lag]
            if (corr > bestCorr) bestCorr = corr
        }
        return sqrt(abs(bestCorr / energy)).coerceIn(0f, 1f)
    }

    private fun computeFlux(spectrum: FloatArray): Float {
        val prev = previousSpectrum
        previousSpectrum = spectrum.copyOf()
        if (prev == null || prev.size != spectrum.size) return 0f
        var flux = 0f
        var meanMag = 0f
        for (i in spectrum.indices) {
            val diff = spectrum[i] - prev[i]
            flux += diff * diff
            meanMag += spectrum[i]
        }
        meanMag /= spectrum.size.coerceAtLeast(1)
        return if (meanMag > 0) sqrt(flux) / (meanMag + 1e-10f) else 0f
    }

    private fun computeChroma(spectrum: FloatArray): FloatArray {
        val chroma = FloatArray(12)
        for (i in spectrum.indices) {
            val freq = i.toFloat() * SAMPLE_RATE / FFT_SIZE
            if (freq < 65f || freq > 4200f) continue
            val midiNote = 69f + 12f * log2(freq / 440f)
            val pitchClass = ((midiNote.roundToInt() % 12) + 12) % 12
            chroma[pitchClass] += spectrum[i] * spectrum[i]
        }
        val maxVal = chroma.maxOrNull() ?: 1f
        if (maxVal > 0) for (i in chroma.indices) chroma[i] /= maxVal
        return chroma
    }

    private fun computeEnvelope(samples: FloatArray): FloatArray {
        val result = FloatArray(5)
        val n = samples.size
        val smoothed = FloatArray(n)
        smoothed[0] = abs(samples[0])
        for (i in 1 until n) smoothed[i] = smoothed[i - 1] * 0.99f + abs(samples[i]) * 0.01f
        var peakVal = 0f
        var peakIdx = 0
        for (i in 0 until n) {
            if (smoothed[i] > peakVal) {
                peakVal = smoothed[i]
                peakIdx = i
            }
        }
        result[0] = if (n > 0) peakIdx.toFloat() / n else 0f
        if (peakIdx < n - 1 && peakVal > 0.001f) {
            val postPeak = minOf(n - peakIdx, n / 4)
            var decaySum = 0f
            for (i in 1 until postPeak) {
                if (peakIdx + i < n) decaySum += smoothed[peakIdx + i] - smoothed[peakIdx + i - 1]
            }
            result[1] = (decaySum / postPeak.coerceAtLeast(1)) / peakVal
        }
        if (peakVal > 0.001f) {
            val ss = minOf(peakIdx + n / 4, n - 1)
            var su = 0f
            var sc = 0
            for (i in ss until n) { su += smoothed[i]; sc++ }
            result[2] = if (sc > 0) (su / sc) / peakVal else 0f
        }
        val rs = n * 3 / 4
        if (rs < n - 1 && smoothed[rs] > 0.001f) {
            result[3] = (smoothed[rs] - smoothed[n - 1]) / smoothed[rs]
        }
        val ws = n / 4
        val we = n * 3 / 4
        if (we > ws) {
            var mn = Float.MAX_VALUE
            var mx = Float.MIN_VALUE
            for (i in ws until we) {
                if (smoothed[i] < mn) mn = smoothed[i]
                if (smoothed[i] > mx) mx = smoothed[i]
            }
            val ct = (mx + mn) / 2f
            result[4] = if (ct > 0.001f) ((mx - mn) / 2f) / ct else 0f
        }
        for (i in result.indices) result[i] = result[i].coerceIn(0f, 1f)
        return result
    }

    private fun computeFilterChars(spectrum: FloatArray): FloatArray {
        val result = FloatArray(4)
        if (spectrum.size < 16) return result
        val se = minOf(spectrum.size, (5000f * FFT_SIZE / SAMPLE_RATE).toInt())
        val logSpec = FloatArray(se) { ln(spectrum[it].coerceAtLeast(1e-10f)) }
        var maxDrop = 0f
        for (i in 10 until se - 10) {
            val drop = logSpec[i - 10] - logSpec[i + 10]
            if (drop > maxDrop) maxDrop = drop
        }
        result[0] = (maxDrop * 8.686f / 40f).coerceIn(0f, 1f)
        var totalE = 0f
        for (v in spectrum) totalE += v * v
        if (totalE > 0) {
            var cu = 0f
            val th = totalE * 0.85f
            for (i in spectrum.indices) {
                cu += spectrum[i] * spectrum[i]
                if (cu >= th) { result[1] = i.toFloat() / spectrum.size; break }
            }
        }
        val pk = spectrum.maxOrNull() ?: 0f
        val av = spectrum.average().toFloat()
        result[2] = if (av > 0) ((pk / av - 1f) / 20f).coerceIn(0f, 1f) else 0f
        var pa = 0f
        var sumSq = 0f
        for (v in spectrum) {
            val a = abs(v)
            if (a > pa) pa = a
            sumSq += v * v
        }
        val rms = sqrt(sumSq / spectrum.size.coerceAtLeast(1))
        result[3] = if (rms > 0) (pa / rms / 20f).coerceIn(0f, 1f) else 0f
        return result
    }

    private fun averageFeatures(allFeatures: List<FloatArray>): FloatArray {
        if (allFeatures.isEmpty()) return FloatArray(FEATURE_COUNT)
        val avg = FloatArray(FEATURE_COUNT)
        for (features in allFeatures) {
            for (i in 0 until minOf(features.size, FEATURE_COUNT)) avg[i] += features[i]
        }
        val count = allFeatures.size.toFloat()
        for (i in avg.indices) avg[i] /= count
        return avg
    }

    fun reset() { previousSpectrum = null }
}

data class SynthFeatureVector(
    val mfccs: FloatArray = FloatArray(13),
    val mfccDeltas: FloatArray = FloatArray(13),
    val mfccDeltaDeltas: FloatArray = FloatArray(13),
    val spectralCentroid: Float = 0f,
    val spectralRollOff: Float = 0f,
    val zeroCrossingRate: Float = 0f,
    val harmonicToNoiseRatio: Float = 0f,
    val spectralFlux: Float = 0f,
    val chroma: FloatArray = FloatArray(12),
    val attackTime: Float = 0f,
    val decaySlope: Float = 0f,
    val sustainLevel: Float = 0f,
    val releaseTime: Float = 0f,
    val tremoloDepth: Float = 0f,
    val filterSlope: Float = 0.5f,
    val resonance: Float = 0f,
    val cutoffFrequency: Float = 0f,
    val filterType: Float = 0f
) {
    companion object {
        fun fromFloatArray(f: FloatArray): SynthFeatureVector = SynthFeatureVector(
            mfccs = f.sliceArray(0 until 13),
            mfccDeltas = f.sliceArray(13 until 26),
            mfccDeltaDeltas = f.sliceArray(26 until 39),
            spectralCentroid = f.getOrElse(39) { 0f },
            spectralRollOff = f.getOrElse(40) { 0f },
            zeroCrossingRate = f.getOrElse(41) { 0f },
            harmonicToNoiseRatio = f.getOrElse(42) { 0f },
            spectralFlux = f.getOrElse(43) { 0f },
            chroma = f.sliceArray(44 until 56),
            attackTime = f.getOrElse(56) { 0f },
            decaySlope = f.getOrElse(57) { 0f },
            sustainLevel = f.getOrElse(58) { 0f },
            releaseTime = f.getOrElse(59) { 0f },
            tremoloDepth = f.getOrElse(60) { 0f },
            filterSlope = f.getOrElse(61) { 0.5f },
            resonance = f.getOrElse(62) { 0f },
            cutoffFrequency = f.getOrElse(63) { 0f },
            filterType = f.getOrElse(64) { 0f }
        )
    }
}
