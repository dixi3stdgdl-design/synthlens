package com.synthlens.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detected_synths")
data class DetectedSynthEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,
    val detectedAt: Long = System.currentTimeMillis(),
    val confidence: Float,
    val frequencySignature: String = "",
    val waveformType: String = "",
    val octaveRange: String = "",
    val filterType: String = "",
    val extraInfo: String = "",
    val isFavorite: Boolean = false
)

@Entity(tableName = "synth_library")
data class SynthLibraryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brand: String,
    val category: String,
    val yearReleased: Int = 0,
    val yearDiscontinued: Int = 0,
    val description: String = "",
    val waveformTypes: String = "",
    val filterTypes: String = "",
    val polyphony: String = "",
    val oscillators: String = "",
    val notableFeatures: String = "",
    val frequencySignature: String = "",
    val isDetected: Boolean = false,
    val detectionCount: Int = 0,
    val imageUrl: String = "",
    val purchaseUrl: String = "",
    val priceRange: String = "",
    val officialSite: String = "",
    val soundDemos: String = "",
    val studioUse: String = "",
    val famousUsers: String = "",
    val pros: String = "",
    val cons: String = "",
    val alternatives: String = "",
    val signalChain: String = "",
    val powerType: String = "",
    val dimensions: String = "",
    val weight: String = "",
    val connectivity: String = "",
    val presets: String = "",
    val genre: String = "",
    val countryOfOrigin: String = "",
    val keyboardType: String = "",
    val bestFor: String = "",
    val soundCharacter: String = "",
    val isClone: Boolean = false,
    val clones: String = ""
)

@Entity(tableName = "detection_history")
data class DetectionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val synthName: String,
    val brand: String,
    val category: String,
    val confidence: Float,
    val waveformType: String = "",
    val frequencyHz: Float = 0f,
    val octave: Int = 0,
    val stemBreakdown: String = "",
    val detectedAt: Long = System.currentTimeMillis(),
    val durationMs: Long = 0,
    val audioFingerprint: String = "",
    val isFavorite: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "audio_recordings")
data class AudioRecordingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val durationMs: Long,
    val sampleRate: Int = 44100,
    val detectedSynth: String = "",
    val confidence: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val title: String = "",
    val tags: String = ""
)

@Entity(tableName = "ab_comparisons")
data class ABComparisonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val synthAName: String,
    val synthABrand: String,
    val synthAWaveform: String = "",
    val synthAFrequency: Float = 0f,
    val synthAConfidence: Float = 0f,
    val synthBName: String,
    val synthBBrand: String,
    val synthBWaveform: String = "",
    val synthBFrequency: Float = 0f,
    val synthBConfidence: Float = 0f,
    val createdAt: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val iconEmoji: String,
    val category: String,
    val requirement: String,
    val progress: Int = 0,
    val target: Int,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val rarity: String = "Common"
)
