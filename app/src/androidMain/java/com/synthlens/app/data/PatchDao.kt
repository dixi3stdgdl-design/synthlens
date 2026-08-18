package com.synthlens.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PatchDao {
    @Query("SELECT * FROM patches ORDER BY timestamp DESC")
    fun getAllPatches(): Flow<List<PatchEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatch(patch: PatchEntity)

    @Query("DELETE FROM patches WHERE id = :patchId")
    suspend fun deletePatch(patchId: String)
}
