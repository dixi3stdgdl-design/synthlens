package com.synthlens.app.engine

import kotlinx.coroutines.flow.StateFlow

data class StemSynthProfile(
    val stemName: String,
    val confidence: Float,
    val energy: Float,
    val frequencyRange: String = "",
    val waveformType: String = "",
    val peakFrequency: Float = 0f,
    val detectedSynth: String = "",
    val brand: String = "",
    val filterType: String = "",
    val characteristics: Map<String, String> = emptyMap()
)

data class HandpanHarmonic(
    val frequency: Float,
    val amplitudeRatio: Float,
    val type: String
)

data class DetectedHandpanResult(
    val brand: String = "",
    val model: String = "",
    val instrumentName: String = "",
    val confidence: Float = 0f,
    val detectedNote: String = "",
    val detectedOctave: Int = 0,
    val centsOffset: Float = 0f,
    val fundamentalHz: Float = 0f,
    val harmonicCount: Int = 0,
    val harmonics: List<HandpanHarmonic> = emptyList(),
    val material: String = "",
    val scale: List<String> = emptyList(),
    val tuningSystem: String = "",
    val sizeCategory: String = "",
    val attackType: String = "",
    val sustainRating: Float = 0f,
    val overtoneRatio: Float = 0f,
    val inharmonicity: Float = 0f,
    val brightnessIndex: Float = 0f,
    val warmthIndex: Float = 0f,
    val spectralProfile: List<Float> = emptyList()
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
    val detectedHandpan: DetectedHandpanResult? = null,
    val stemAnalysis: StemAnalysis? = null,
    val stemProfiles: List<StemSynthProfile> = emptyList(),
    val dominantStemName: String? = null,
    val spectralFlatness: Float = 0f,
    val spectralRolloff: Float = 0f,
    val spectralBandwidth: Float = 0f,
    val harmonicToNoiseRatio: Float = 0f,
    val noteName: String = "",
    val harmonicCount: Int = 0
)

interface AudioEngine {
    val analysis: StateFlow<AudioAnalysis>
    val isRecording: StateFlow<Boolean>
    
    fun startRecording()
    fun stopRecording()
    fun toggleRecording()
    fun destroy()
}
