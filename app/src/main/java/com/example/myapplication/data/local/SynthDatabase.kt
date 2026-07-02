package com.example.myapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        DetectedSynthEntity::class,
        SynthLibraryEntity::class,
        DetectionHistoryEntity::class,
        AudioRecordingEntity::class,
        ABComparisonEntity::class,
        AchievementEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SynthDatabase : RoomDatabase() {
    abstract fun detectedSynthDao(): DetectedSynthDao
    abstract fun synthLibraryDao(): SynthLibraryDao
    abstract fun detectionHistoryDao(): DetectionHistoryDao
    abstract fun audioRecordingDao(): AudioRecordingDao
    abstract fun abComparisonDao(): ABComparisonDao
    abstract fun achievementDao(): AchievementDao

    companion object {
        @Volatile
        private var INSTANCE: SynthDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE synth_library ADD COLUMN yearDiscontinued INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN genre TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN countryOfOrigin TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN keyboardType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN bestFor TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN soundCharacter TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN isClone INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE synth_library ADD COLUMN clones TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE detected_synths ADD COLUMN frequencySignature TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE detected_synths ADD COLUMN waveformType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE detected_synths ADD COLUMN octaveRange TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE detected_synths ADD COLUMN filterType TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE detected_synths ADD COLUMN extraInfo TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS detection_history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        synthName TEXT NOT NULL,
                        brand TEXT NOT NULL,
                        category TEXT NOT NULL,
                        confidence REAL NOT NULL,
                        waveformType TEXT NOT NULL DEFAULT '',
                        frequencyHz REAL NOT NULL DEFAULT 0,
                        octave INTEGER NOT NULL DEFAULT 0,
                        stemBreakdown TEXT NOT NULL DEFAULT '',
                        detectedAt INTEGER NOT NULL DEFAULT 0,
                        durationMs INTEGER NOT NULL DEFAULT 0,
                        audioFingerprint TEXT NOT NULL DEFAULT '',
                        isFavorite INTEGER NOT NULL DEFAULT 0,
                        notes TEXT NOT NULL DEFAULT ''
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS audio_recordings (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        filePath TEXT NOT NULL,
                        durationMs INTEGER NOT NULL,
                        sampleRate INTEGER NOT NULL DEFAULT 44100,
                        detectedSynth TEXT NOT NULL DEFAULT '',
                        confidence REAL NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        title TEXT NOT NULL DEFAULT '',
                        tags TEXT NOT NULL DEFAULT ''
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS ab_comparisons (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        synthAName TEXT NOT NULL,
                        synthABrand TEXT NOT NULL,
                        synthAWaveform TEXT NOT NULL DEFAULT '',
                        synthAFrequency REAL NOT NULL DEFAULT 0,
                        synthAConfidence REAL NOT NULL DEFAULT 0,
                        synthBName TEXT NOT NULL,
                        synthBBrand TEXT NOT NULL,
                        synthBWaveform TEXT NOT NULL DEFAULT '',
                        synthBFrequency REAL NOT NULL DEFAULT 0,
                        synthBConfidence REAL NOT NULL DEFAULT 0,
                        createdAt INTEGER NOT NULL DEFAULT 0,
                        notes TEXT NOT NULL DEFAULT ''
                    )
                """)
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS achievements (
                        id TEXT NOT NULL PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        iconEmoji TEXT NOT NULL,
                        category TEXT NOT NULL,
                        requirement TEXT NOT NULL,
                        progress INTEGER NOT NULL DEFAULT 0,
                        target INTEGER NOT NULL,
                        isUnlocked INTEGER NOT NULL DEFAULT 0,
                        unlockedAt INTEGER,
                        rarity TEXT NOT NULL DEFAULT 'Common'
                    )
                """)
            }
        }

        private val ALL_MIGRATIONS = arrayOf(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4
        )

        fun getInstance(context: Context): SynthDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SynthDatabase::class.java,
                    "synthlens_database"
                )
                    .addMigrations(*ALL_MIGRATIONS)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
