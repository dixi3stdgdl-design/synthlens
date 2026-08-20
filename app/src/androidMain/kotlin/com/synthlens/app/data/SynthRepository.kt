package com.synthlens.app.data

import android.content.Context
import com.synthlens.app.data.local.SynthDatabase
import com.synthlens.app.data.local.toDomain
import com.synthlens.app.data.local.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SynthRepository(private val context: Context) : ISynthRepository {

    private val database = SynthDatabase.getInstance(context)
    private val detectedDao = database.detectedSynthDao()
    private val libraryDao = database.synthLibraryDao()
    private val historyDao = database.detectionHistoryDao()
    private val recordingDao = database.audioRecordingDao()
    private val abDao = database.abComparisonDao()
    private val achievementDao = database.achievementDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    override val allSynths: Flow<List<SynthLibraryItem>> = libraryDao.getAllSynths().map { entities ->
        entities.map { it.toDomain() }
    }

    override val detectedSynths: Flow<List<DetectedSynth>> = detectedDao.getAllDetected().map { entities ->
        entities.map { it.toDomain() }
    }

    override val allHistory: Flow<List<DetectionHistory>> = historyDao.getAllHistory().map { entities ->
        entities.map { it.toDomain() }
    }

    override val favoriteHistory: Flow<List<DetectionHistory>> = historyDao.getFavorites().map { entities ->
        entities.map { it.toDomain() }
    }

    override val allRecordings: Flow<List<AudioRecording>> = recordingDao.getAllRecordings().map { entities ->
        entities.map { it.toDomain() }
    }

    override val allComparisons: Flow<List<ABComparison>> = abDao.getAllComparisons().map { entities ->
        entities.map { it.toDomain() }
    }

    override val detectedCount: StateFlow<Int> = libraryDao.getDetectedCountFlow()
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)

    override val totalCount: StateFlow<Int> = libraryDao.getCountFlow()
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), 0)

    private val achievementRepository = AchievementRepository(achievementDao)

    init {
        scope.launch {
            initializeDatabase()
        }
    }

    private suspend fun initializeDatabase() {
        val count = libraryDao.getCount()
        if (count == 0) {
            val synths = SynthDatabaseSeeder.getAllSynths()
            libraryDao.insertAll(synths.map { it.toEntity() })
        }
        achievementRepository.seedAchievements()
    }

    override fun addDetectedSynth(synth: DetectedSynth) {
        scope.launch {
            detectedDao.insert(synth.toEntity())
            libraryDao.markAsDetected(synth.brand, synth.name)
        }
    }

    override fun addDetectionHistory(history: DetectionHistory) {
        scope.launch {
            historyDao.insert(history.toEntity())
        }
    }

    override fun toggleHistoryFavorite(id: Long, isFavorite: Boolean) {
        scope.launch {
            historyDao.setFavorite(id, isFavorite)
        }
    }

    override fun deleteHistoryEntry(entry: DetectionHistory) {
        scope.launch {
            historyDao.delete(entry.toEntity())
        }
    }

    override fun searchHistory(query: String): Flow<List<DetectionHistory>> {
        return historyDao.searchHistory(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun addRecording(recording: AudioRecording) {
        scope.launch {
            recordingDao.insert(recording.toEntity())
        }
    }

    override fun deleteRecording(id: Long) {
        scope.launch {
            recordingDao.deleteById(id)
        }
    }

    override fun addComparison(comparison: ABComparison) {
        scope.launch {
            abDao.insert(comparison.toEntity())
        }
    }

    override fun deleteComparison(comparison: ABComparison) {
        scope.launch {
            abDao.delete(comparison.toEntity())
        }
    }

    override fun markAsDetected(brand: String, name: String) {
        scope.launch {
            libraryDao.markAsDetected(brand, name)
        }
    }

    override fun getSynthsByBrand(brand: String): Flow<List<SynthLibraryItem>> {
        return libraryDao.getSynthsByBrand(brand).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchSynths(query: String): Flow<List<SynthLibraryItem>> {
        return libraryDao.searchSynths(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getDetectedSynthItems(): Flow<List<SynthLibraryItem>> {
        return libraryDao.getDetectedSynths().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllBrands(): Flow<List<String>> {
        return libraryDao.getAllBrands()
    }

    suspend fun getSynthsByBrandSync(brand: String): List<SynthLibraryItem> {
        return libraryDao.getSynthsByBrand(brand).first().map { it.toDomain() }
    }

    suspend fun searchSynthsSync(query: String): List<SynthLibraryItem> {
        return libraryDao.searchSynths(query).first().map { it.toDomain() }
    }

    suspend fun getDetectedSynthItemsSync(): List<SynthLibraryItem> {
        return libraryDao.getDetectedSynths().first().map { it.toDomain() }
    }

    suspend fun getRecentHistory(limit: Int = 50): List<DetectionHistory> {
        return historyDao.getRecentHistory(limit).first().map { it.toDomain() }
    }

    // Achievement methods
    override fun getAllAchievements(): Flow<List<com.synthlens.app.data.AchievementEntity>> {
        return achievementRepository.getAllAchievements().map { entities ->
            entities.map { entity ->
                com.synthlens.app.data.AchievementEntity(
                    id = entity.id,
                    name = entity.name,
                    description = entity.description,
                    isUnlocked = entity.isUnlocked,
                    rarity = entity.rarity,
                    progress = entity.progress,
                    target = entity.target,
                    iconEmoji = entity.iconEmoji,
                    unlockedAt = entity.unlockedAt ?: 0L,
                    category = entity.category
                )
            }
        }
    }

    fun getUnlockedAchievements() = achievementRepository.getUnlockedAchievements()

    fun getAchievementsByCategory(category: String) = achievementRepository.getAchievementsByCategory(category)

    override suspend fun updateAchievementProgress(id: String, progress: Int) {
        achievementRepository.updateProgress(id, progress)
    }

    suspend fun getUnlockedCount() = achievementRepository.getUnlockedCount()

    suspend fun getTotalCount() = achievementRepository.getTotalCount()

    private fun <T> runBlocking(block: suspend () -> T): T {
        return kotlinx.coroutines.runBlocking(Dispatchers.IO) { block() }
    }

    companion object {
        @Volatile
        private var INSTANCE: SynthRepository? = null

        fun getInstance(context: Context): SynthRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SynthRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
