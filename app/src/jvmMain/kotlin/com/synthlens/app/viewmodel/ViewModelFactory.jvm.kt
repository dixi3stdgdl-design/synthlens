package com.synthlens.app.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.synthlens.app.data.ISynthRepository
import com.synthlens.app.data.SynthLibraryItem
import com.synthlens.app.data.DetectedSynth
import com.synthlens.app.data.DetectionHistory
import com.synthlens.app.data.AudioRecording
import com.synthlens.app.data.ABComparison
import com.synthlens.app.data.AchievementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow

class JvmSynthRepository : ISynthRepository {
    override val allSynths: Flow<List<SynthLibraryItem>> = emptyFlow()
    override val detectedSynths: Flow<List<DetectedSynth>> = emptyFlow()
    override val allHistory: Flow<List<DetectionHistory>> = emptyFlow()
    override val favoriteHistory: Flow<List<DetectionHistory>> = emptyFlow()
    override val allRecordings: Flow<List<AudioRecording>> = emptyFlow()
    override val allComparisons: Flow<List<ABComparison>> = emptyFlow()
    override val detectedCount: StateFlow<Int> = MutableStateFlow(0)
    override val totalCount: StateFlow<Int> = MutableStateFlow(0)

    override fun addDetectedSynth(synth: DetectedSynth) {}
    override fun addDetectionHistory(history: DetectionHistory) {}
    override fun toggleHistoryFavorite(id: Long, isFavorite: Boolean) {}
    override fun deleteHistoryEntry(entry: DetectionHistory) {}
    override fun searchHistory(query: String): Flow<List<DetectionHistory>> = emptyFlow()
    override fun addRecording(recording: AudioRecording) {}
    override fun deleteRecording(id: Long) {}
    override fun addComparison(comparison: ABComparison) {}
    override fun deleteComparison(comparison: ABComparison) {}
    override fun markAsDetected(brand: String, name: String) {}
    override fun getSynthsByBrand(brand: String): Flow<List<SynthLibraryItem>> = emptyFlow()
    override fun searchSynths(query: String): Flow<List<SynthLibraryItem>> = emptyFlow()
    override fun getDetectedSynthItems(): Flow<List<SynthLibraryItem>> = emptyFlow()
    override fun getAllBrands(): Flow<List<String>> = emptyFlow()

    override fun getAllAchievements(): Flow<List<AchievementEntity>> = emptyFlow()
    override suspend fun updateAchievementProgress(id: String, progress: Int) {}
}

@Composable
actual fun createSynthViewModel(): SynthViewModel {
    return remember { SynthViewModel(JvmSynthRepository()) }
}
