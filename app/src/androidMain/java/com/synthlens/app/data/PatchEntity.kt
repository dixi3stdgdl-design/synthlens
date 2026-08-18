package com.synthlens.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patches")
data class PatchEntity(
    @PrimaryKey
    val id: String,
    val synthName: String,
    val author: String,
    val timestamp: Long,
    val confidence: Float
)
