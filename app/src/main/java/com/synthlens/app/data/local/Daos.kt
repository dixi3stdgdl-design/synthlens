package com.synthlens.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectedSynthDao {
    @Query("SELECT * FROM detected_synths ORDER BY detectedAt DESC")
    fun getAllDetected(): Flow<List<DetectedSynthEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(synth: DetectedSynthEntity): Long

    @Update
    suspend fun update(synth: DetectedSynthEntity)

    @Delete
    suspend fun delete(synth: DetectedSynthEntity)

    @Query("DELETE FROM detected_synths")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM detected_synths")
    suspend fun getCount(): Int
}

@Dao
interface SynthLibraryDao {
    @Query("SELECT * FROM synth_library ORDER BY brand, name")
    fun getAllSynths(): Flow<List<SynthLibraryEntity>>

    @Query("SELECT * FROM synth_library WHERE brand = :brand ORDER BY name")
    fun getSynthsByBrand(brand: String): Flow<List<SynthLibraryEntity>>

    @Query("SELECT * FROM synth_library WHERE name LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchSynths(query: String): Flow<List<SynthLibraryEntity>>

    @Query("SELECT * FROM synth_library WHERE isDetected = 1")
    fun getDetectedSynths(): Flow<List<SynthLibraryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(synths: List<SynthLibraryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(synth: SynthLibraryEntity): Long

    @Update
    suspend fun update(synth: SynthLibraryEntity)

    @Query("UPDATE synth_library SET isDetected = 1, detectionCount = detectionCount + 1 WHERE brand = :brand AND name = :name")
    suspend fun markAsDetected(brand: String, name: String)

    @Query("SELECT COUNT(*) FROM synth_library")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM synth_library")
    fun getCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM synth_library WHERE isDetected = 1")
    suspend fun getDetectedCount(): Int

    @Query("SELECT COUNT(*) FROM synth_library WHERE isDetected = 1")
    fun getDetectedCountFlow(): Flow<Int>

    @Query("SELECT DISTINCT brand FROM synth_library ORDER BY brand")
    fun getAllBrands(): Flow<List<String>>
}

@Dao
interface DetectionHistoryDao {
    @Query("SELECT * FROM detection_history ORDER BY detectedAt DESC")
    fun getAllHistory(): Flow<List<DetectionHistoryEntity>>

    @Query("SELECT * FROM detection_history ORDER BY detectedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int): Flow<List<DetectionHistoryEntity>>

    @Query("SELECT * FROM detection_history WHERE synthName LIKE '%' || :query || '%' OR brand LIKE '%' || :query || '%'")
    fun searchHistory(query: String): Flow<List<DetectionHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DetectionHistoryEntity): Long

    @Delete
    suspend fun delete(entry: DetectionHistoryEntity)

    @Query("DELETE FROM detection_history")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM detection_history")
    suspend fun getCount(): Int

    @Query("SELECT * FROM detection_history WHERE isFavorite = 1 ORDER BY detectedAt DESC")
    fun getFavorites(): Flow<List<DetectionHistoryEntity>>

    @Query("UPDATE detection_history SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun setFavorite(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM detection_history WHERE id = :id")
    suspend fun getById(id: Long): DetectionHistoryEntity?
}

@Dao
interface AudioRecordingDao {
    @Query("SELECT * FROM audio_recordings ORDER BY createdAt DESC")
    fun getAllRecordings(): Flow<List<AudioRecordingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recording: AudioRecordingEntity): Long

    @Delete
    suspend fun delete(recording: AudioRecordingEntity)

    @Query("DELETE FROM audio_recordings WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM audio_recordings")
    suspend fun getCount(): Int
}

@Dao
interface ABComparisonDao {
    @Query("SELECT * FROM ab_comparisons ORDER BY createdAt DESC")
    fun getAllComparisons(): Flow<List<ABComparisonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(comparison: ABComparisonEntity): Long

    @Delete
    suspend fun delete(comparison: ABComparisonEntity)
}

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements ORDER BY category, name")
    fun getAllAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedAchievements(): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE category = :category ORDER BY name")
    fun getAchievementsByCategory(category: String): Flow<List<AchievementEntity>>

    @Query("SELECT * FROM achievements WHERE id = :id")
    suspend fun getById(id: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(achievement: AchievementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(achievements: List<AchievementEntity>)

    @Update
    suspend fun update(achievement: AchievementEntity)

    @Query("UPDATE achievements SET progress = :progress, isUnlocked = CASE WHEN :progress >= target THEN 1 ELSE isUnlocked END, unlockedAt = CASE WHEN :progress >= target AND isUnlocked = 0 THEN :unlockedAt ELSE unlockedAt END WHERE id = :id")
    suspend fun updateProgress(id: String, progress: Int, unlockedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM achievements WHERE isUnlocked = 1")
    suspend fun getUnlockedCount(): Int

    @Query("SELECT COUNT(*) FROM achievements")
    suspend fun getTotalCount(): Int
}
