package com.synthlens.app.data

import android.content.Context
import androidx.room.Room
import com.synthlens.app.data.db.AppDatabase
import com.synthlens.app.data.db.PatchDao
import com.synthlens.app.data.db.PatchEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CloudPatch(
    val id: String,
    val synthName: String,
    val author: String,
    val timestamp: Long,
    val confidence: Float
)

class CloudWorkspaceRepository(private val context: Context) {
    
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "synthlens-workspace-db"
    ).build()
    
    private val patchDao: PatchDao = db.patchDao()

    fun syncWorkspace(): Flow<List<CloudPatch>> {
        return patchDao.getAllPatches().map { entities ->
            entities.map {
                CloudPatch(it.id, it.synthName, it.author, it.timestamp, it.confidence)
            }
        }
    }
    
    suspend fun savePatch(patch: CloudPatch) {
        val entity = PatchEntity(
            id = patch.id,
            synthName = patch.synthName,
            author = patch.author,
            timestamp = patch.timestamp,
            confidence = patch.confidence
        )
        patchDao.insertPatch(entity)
    }
}
