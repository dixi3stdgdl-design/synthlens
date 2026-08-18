package com.synthlens.app.engine

data class SongResult(
    val title: String,
    val artist: String,
    val albumArtUrl: String?,
    val timecode: String? = null
)

interface SongRecognizer {
    suspend fun identifySong(pcmData: ByteArray): SongResult?
}
