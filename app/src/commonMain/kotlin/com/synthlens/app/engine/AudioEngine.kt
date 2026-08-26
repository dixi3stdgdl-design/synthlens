package com.synthlens.app.engine

import kotlinx.coroutines.flow.StateFlow

data class StemSynthProfile(
    val stemName: String,
    val detectedSynth: String = "",
    val brand: String = "",
    val category: String = "",
    val confidence: Float,
    val waveformType: String = "",
    val filterType: String = "",
    val frequencyRange: String = "",
    val peakFrequency: Float = 0f,
    val harmonics: List<Float> = emptyList(),
    val thd: Float = 0f,
    val rmsLevel: Float = 0f,
    val energy: Float,
    val characteristics: Map<String, String> = emptyMap()
)

data class StemAnalysis(
    val stems: List<StemSynthProfile>,
    val separationConfidence: Float = 0f
)

data class DetectedSynthResult(
    val name: String,
    val brand: String,
    val category: String,
    val confidence: Float,
    val frequencySignature: String,
    val waveformType: String,
    val filterType: String,
    val oscillators: String,
    val modulation: String,
    val daw: String,
    val effects: String,
    val pattern: String
)

data class AudioAnalysis(
    val frequency: Float = 0f,
    val amplitude: Float = 0f,
    val waveformType: String = "Unknown",
    val octaves: Int = 0,
    val rmsLevel: Float = 0f,
    val peakLevel: Float = 0f,
    val thd: Float = 0f,
    val spectrumData: FloatArray = FloatArray(0),
    val waveformPoints: List<Float> = emptyList(),
    val harmonics: List<Float> = emptyList(),
    val isDetecting: Boolean = false,
    val detectionPhase: Int = 0,
    val detectedSong: String? = null,
    val detectedArtist: String? = null,
    val detectedSynth: DetectedSynthResult? = null,

    val stemAnalysis: StemAnalysis? = null,
    val stemProfiles: List<StemSynthProfile> = emptyList(),
    val dominantStemName: String? = null,
    val spectralFlatness: Float = 0f,
    val spectralRolloff: Float = 0f,
    val spectralBandwidth: Float = 0f,
    val harmonicToNoiseRatio: Float = 0f,
    val noteName: String = "",
    val harmonicCount: Int = 0,
    
    // AI Provenance Detection Metrics
    val aiProbability: Float = 0f,
    val phaseCoherence: Float = 1f,
    val highFrequencyRolloff: Float = 0f,
    val transientSharpness: Float = 1f
)

interface AudioEngine {
    val analysis: StateFlow<AudioAnalysis>
    val isRecording: StateFlow<Boolean>
    
    fun startRecording()
    fun stopRecording()
    fun toggleRecording()
    fun destroy()
}
