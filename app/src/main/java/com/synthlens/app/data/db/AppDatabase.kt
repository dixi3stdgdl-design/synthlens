package com.synthlens.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PatchEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patchDao(): PatchDao
}
