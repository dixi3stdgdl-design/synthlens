package com.synthlens.app.engine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.math.abs
import kotlin.math.sqrt

class JvmAudioEngine : AudioEngine {
    private val _analysis = MutableStateFlow(AudioAnalysis())
    override val analysis: StateFlow<AudioAnalysis> = _analysis.asStateFlow()

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private var targetLine: TargetDataLine? = null
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun startRecording() {
        if (_isRecording.value) return

        try {
            val format = AudioFormat(44100f, 16, 1, true, false)
            val info = DataLine.Info(TargetDataLine::class.java, format)
            if (!AudioSystem.isLineSupported(info)) {
                println("TargetDataLine not supported on this JVM")
                return
            }

            targetLine = AudioSystem.getLine(info) as TargetDataLine
            targetLine?.open(format)
            targetLine?.start()
            _isRecording.value = true

            job = scope.launch {
                val buffer = ByteArray(2048)
                while (isActive && _isRecording.value) {
                    val bytesRead = targetLine?.read(buffer, 0, buffer.size) ?: 0
                    if (bytesRead > 0) {
                        processAudioBuffer(buffer, bytesRead)
                    }
                    delay(16) // roughly 60fps
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            _isRecording.value = false
        }
    }

    private fun processAudioBuffer(buffer: ByteArray, bytesRead: Int) {
        val floatArray = FloatArray(bytesRead / 2)
        var sumSquares = 0f
        var maxAmplitude = 0f

        for (i in 0 until bytesRead / 2) {
            val low = buffer[i * 2].toInt()
            val high = buffer[i * 2 + 1].toInt()
            val sample = (high shl 8) or (low and 0xFF)
            val floatSample = sample / 32768f
            floatArray[i] = floatSample

            sumSquares += floatSample * floatSample
            val absSample = abs(floatSample)
            if (absSample > maxAmplitude) {
                maxAmplitude = absSample
            }
        }

        val rms = sqrt(sumSquares / floatArray.size)
        val amplitude = rms * 2f

        // Simulated spectrum data for visualization (FFT would go here for real implementation)
        // Also simulate AI Provenance Detection Features
        var highFreqEnergy = 0f
        var lowFreqEnergy = 0f
        var phaseJitter = 0f
        
        val spectrum = FloatArray(128) { i ->
            val randomNoise = Math.random().toFloat() * 0.1f
            val baseLevel = if (i < 20) amplitude * 0.8f else amplitude * (0.3f / (i - 19))
            val value = baseLevel + randomNoise
            
            if (i > 100) highFreqEnergy += value
            else lowFreqEnergy += value
            
            phaseJitter += (Math.random().toFloat() * 0.05f)
            
            value
        }
        
        // AI Audio Detection Heuristics
        // 1. High Frequency Rolloff: AI models usually cut off high frequencies (e.g. above 16kHz)
        val hfRolloff = if (lowFreqEnergy > 0) 1f - (highFreqEnergy / (lowFreqEnergy * 0.2f)).coerceIn(0f, 1f) else 1f
        
        // 2. Phase Coherence: AI uses vocoders which smear phase compared to analog oscillators
        val coherence = 1f - phaseJitter.coerceIn(0f, 1f)
        
        // 3. Transient Sharpness: Analog attacks are near-instant voltage spikes. AI attacks are smeared.
        // We simulate a measurement by comparing peak vs rms ratio (Crest Factor)
        val crestFactor = if (rms > 0.001f) maxAmplitude / rms else 1f
        val transientSharpness = (crestFactor / 10f).coerceIn(0f, 1f)
        
        // 4. Overall AI Probability (Weighted average of artifacts)
        // High rolloff + Low coherence + Low sharpness = High AI Probability
        var aiProb = (hfRolloff * 0.4f) + ((1f - coherence) * 0.4f) + ((1f - transientSharpness) * 0.2f)
        
        // Add random fluctuation for realism in UI if there's audio, otherwise decay to 0
        if (amplitude > 0.01f) {
            aiProb = (aiProb + (Math.random().toFloat() * 0.1f - 0.05f)).coerceIn(0f, 1f)
        } else {
            aiProb = 0f
        }

        // Sub-sample waveform for UI
        val waveformPoints = mutableListOf<Float>()
        val step = maxOf(1, floatArray.size / 100)
        for (i in floatArray.indices step step) {
            waveformPoints.add(floatArray[i])
        }

        _analysis.value = AudioAnalysis(
            amplitude = amplitude,
            rmsLevel = rms,
            peakLevel = maxAmplitude,
            spectrumData = spectrum,
            waveformPoints = waveformPoints,
            aiProbability = aiProb,
            phaseCoherence = coherence,
            highFrequencyRolloff = hfRolloff,
            transientSharpness = transientSharpness
        )
    }

    override fun stopRecording() {
        _isRecording.value = false
        job?.cancel()
        job = null
        targetLine?.stop()
        targetLine?.close()
        targetLine = null
    }

    override fun toggleRecording() {
        if (_isRecording.value) stopRecording() else startRecording()
    }

    override fun destroy() {
        stopRecording()
        scope.cancel()
    }
}

