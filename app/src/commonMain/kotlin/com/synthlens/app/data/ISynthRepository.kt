package com.synthlens.app.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ISynthRepository {
    val allSynths: Flow<List<SynthLibraryItem>>
    val detectedSynths: Flow<List<DetectedSynth>>
    val allHistory: Flow<List<DetectionHistory>>
    val favoriteHistory: Flow<List<DetectionHistory>>
    val allRecordings: Flow<List<AudioRecording>>
    val allComparisons: Flow<List<ABComparison>>
    val detectedCount: StateFlow<Int>
    val totalCount: StateFlow<Int>

    fun addDetectedSynth(synth: DetectedSynth)
    fun addDetectionHistory(history: DetectionHistory)
    fun toggleHistoryFavorite(id: Long, isFavorite: Boolean)
    fun deleteHistoryEntry(entry: DetectionHistory)
    fun searchHistory(query: String): Flow<List<DetectionHistory>>
    fun addRecording(recording: AudioRecording)
    fun deleteRecording(id: Long)
    fun addComparison(comparison: ABComparison)
    fun deleteComparison(comparison: ABComparison)
    fun markAsDetected(brand: String, name: String)
    fun getSynthsByBrand(brand: String): Flow<List<SynthLibraryItem>>
    fun searchSynths(query: String): Flow<List<SynthLibraryItem>>
    fun getDetectedSynthItems(): Flow<List<SynthLibraryItem>>
    fun getAllBrands(): Flow<List<String>>

    fun getAllAchievements(): Flow<List<AchievementEntity>>
    suspend fun updateAchievementProgress(id: String, progress: Int)
}
