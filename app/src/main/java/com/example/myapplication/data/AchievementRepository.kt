package com.example.myapplication.data

import com.example.myapplication.data.local.AchievementDao
import com.example.myapplication.data.local.AchievementEntity
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val achievementDao: AchievementDao) {

    fun getAllAchievements(): Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

    fun getUnlockedAchievements(): Flow<List<AchievementEntity>> = achievementDao.getUnlockedAchievements()

    fun getAchievementsByCategory(category: String): Flow<List<AchievementEntity>> = achievementDao.getAchievementsByCategory(category)

    suspend fun getById(id: String): AchievementEntity? = achievementDao.getById(id)

    suspend fun updateProgress(id: String, progress: Int) {
        achievementDao.updateProgress(id, progress)
    }

    suspend fun getUnlockedCount(): Int = achievementDao.getUnlockedCount()

    suspend fun getTotalCount(): Int = achievementDao.getTotalCount()

    suspend fun seedAchievements() {
        val count = achievementDao.getTotalCount()
        if (count > 0) return

        val achievements = listOf(
            // Detection achievements
            AchievementEntity(
                id = "first_detection",
                name = "First Contact",
                description = "Detect your first synthesizer",
                iconEmoji = "🎯",
                category = "Detection",
                requirement = "Detect 1 synth",
                target = 1,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "detect_10",
                name = "Synth Spotter",
                description = "Detect 10 different synthesizers",
                iconEmoji = "🔍",
                category = "Detection",
                requirement = "Detect 10 synths",
                target = 10,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "detect_50",
                name = "Synth Hunter",
                description = "Detect 50 different synthesizers",
                iconEmoji = "🏆",
                category = "Detection",
                requirement = "Detect 50 synths",
                target = 50,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "detect_100",
                name = "Synth Legend",
                description = "Detect 100 different synthesizers",
                iconEmoji = "👑",
                category = "Detection",
                requirement = "Detect 100 synths",
                target = 100,
                rarity = "Epic"
            ),

            // Brand achievements
            AchievementEntity(
                id = "brand_moog",
                name = "Moog Master",
                description = "Detect all Moog synthesizers in the database",
                iconEmoji = "🎹",
                category = "Brands",
                requirement = "Detect all Moog synths",
                target = 8,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "brand_roland",
                name = "Roland Royal",
                description = "Detect all Roland synthesizers in the database",
                iconEmoji = "🎵",
                category = "Brands",
                requirement = "Detect all Roland synths",
                target = 6,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "brand_korg",
                name = "Korg King",
                description = "Detect all Korg synthesizers in the database",
                iconEmoji = "🎶",
                category = "Brands",
                requirement = "Detect all Korg synths",
                target = 6,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "brand_sequential",
                name = "Sequential Sensei",
                description = "Detect all Sequential synthesizers",
                iconEmoji = "⚡",
                category = "Brands",
                requirement = "Detect all Sequential synths",
                target = 4,
                rarity = "Rare"
            ),

            // Waveform achievements
            AchievementEntity(
                id = "waveform_saw",
                name = "Sawtooth Specialist",
                description = "Detect 20 sawtooth waveforms",
                iconEmoji = "📈",
                category = "Waveforms",
                requirement = "Detect 20 saw waveforms",
                target = 20,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "waveform_square",
                name = "Square Wizard",
                description = "Detect 20 square waveforms",
                iconEmoji = "📊",
                category = "Waveforms",
                requirement = "Detect 20 square waveforms",
                target = 20,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "waveform_all",
                name = "Waveform Connoisseur",
                description = "Detect all 5 waveform types (Saw, Square, Triangle, Sine, Pulse)",
                iconEmoji = "🌊",
                category = "Waveforms",
                requirement = "Detect all waveform types",
                target = 5,
                rarity = "Rare"
            ),

            // Session achievements
            AchievementEntity(
                id = "session_1h",
                name = "Dedicated Listener",
                description = "Use the app for 1 hour total",
                iconEmoji = "⏰",
                category = "Sessions",
                requirement = "1 hour total usage",
                target = 60,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "session_10h",
                name = "Audio Addict",
                description = "Use the app for 10 hours total",
                iconEmoji = "🎧",
                category = "Sessions",
                requirement = "10 hours total usage",
                target = 600,
                rarity = "Rare"
            ),

            // Confidence achievements
            AchievementEntity(
                id = "high_confidence",
                name = "Crystal Clear",
                description = "Detect a synth with 90%+ confidence",
                iconEmoji = "💎",
                category = "Quality",
                requirement = "90%+ confidence detection",
                target = 1,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "perfect_detection",
                name = "Perfect Pitch",
                description = "Detect a synth with 99%+ confidence",
                iconEmoji = "🎯",
                category = "Quality",
                requirement = "99%+ confidence detection",
                target = 1,
                rarity = "Epic"
            ),

            // Special achievements
            AchievementEntity(
                id = "night_owl",
                name = "Night Owl",
                description = "Use the app between midnight and 5 AM",
                iconEmoji = "🦉",
                category = "Special",
                requirement = "Use app at night",
                target = 1,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "early_bird",
                name = "Early Bird",
                description = "Use the app before 6 AM",
                iconEmoji = "🐦",
                category = "Special",
                requirement = "Use app early morning",
                target = 1,
                rarity = "Common"
            ),
            AchievementEntity(
                id = "library_browse",
                name = "Library Legend",
                description = "View 50 synth detail pages",
                iconEmoji = "📚",
                category = "Exploration",
                requirement = "View 50 synth details",
                target = 50,
                rarity = "Rare"
            ),
            AchievementEntity(
                id = "favorite_10",
                name = "Top Ten",
                description = "Add 10 synths to favorites",
                iconEmoji = "⭐",
                category = "Collection",
                requirement = "Favorite 10 synths",
                target = 10,
                rarity = "Common"
            )
        )

        achievementDao.insertAll(achievements)
    }
}