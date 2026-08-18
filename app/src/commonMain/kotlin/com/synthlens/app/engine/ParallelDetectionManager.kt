package com.synthlens.app.engine

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class DetectionState {
    IDLE,
    IDENTIFYING_SONG,
    SONG_FOUND_ANALYZING_STEMS,
    COMPLETE,
    ERROR
}

data class ParallelDetectionResult(
    val state: DetectionState = DetectionState.IDLE,
    val song: SongResult? = null,
    val stemAnalysis: StemAnalysis? = null,
    val detectedHandpan: DetectedHandpanResult? = null
)

class ParallelDetectionManager(
    private val songRecognizer: SongRecognizer,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val _result = MutableStateFlow(ParallelDetectionResult())
    val result: StateFlow<ParallelDetectionResult> = _result.asStateFlow()
    
    private var detectionJob: Job? = null

    fun startDetection(pcmData: ByteArray, simulateMLDelayMs: Long = 4000) {
        if (detectionJob?.isActive == true) return
        
        _result.value = ParallelDetectionResult(state = DetectionState.IDENTIFYING_SONG)
        
        detectionJob = scope.launch {
            try {
                // Track 1: Fast song identification
                val songDeferred = async { songRecognizer.identifySong(pcmData) }
                
                // Track 2: Heavy ML stem separation (Simulated for now, replacing with real ML inference soon)
                val stemDeferred = async { 
                    delay(simulateMLDelayMs) 
                    StemAnalysis(
                        stems = listOf(
                            StemSynthProfile(
                                stemName = "Bass", 
                                confidence = 0.95f, 
                                energy = 0.8f, 
                                frequencyRange = "20-200Hz", 
                                waveformType = "Sawtooth", 
                                peakFrequency = 55f, 
                                detectedSynth = "Moog Sub 37", 
                                brand = "Moog"
                            ),
                            StemSynthProfile(
                                stemName = "Lead", 
                                confidence = 0.90f, 
                                energy = 0.6f, 
                                frequencyRange = "400-2000Hz", 
                                waveformType = "Square", 
                                peakFrequency = 880f, 
                                detectedSynth = "Prophet 5", 
                                brand = "Sequential"
                            )
                        )
                    )
                }

                val songResult = songDeferred.await()
                if (songResult != null) {
                    _result.value = _result.value.copy(
                        state = DetectionState.SONG_FOUND_ANALYZING_STEMS,
                        song = songResult
                    )
                }
                
                val stemResult = stemDeferred.await()
                _result.value = _result.value.copy(
                    state = DetectionState.COMPLETE,
                    stemAnalysis = stemResult
                )

            } catch (e: Exception) {
                _result.value = _result.value.copy(state = DetectionState.ERROR)
            }
        }
    }

    fun reset() {
        detectionJob?.cancel()
        _result.value = ParallelDetectionResult()
    }
}
