package com.synthlens.app.engine

import io.ktor.client.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay

class AuddSongRecognizer(
    private val client: HttpClient,
    private val apiKey: String = "test_api_key_here"
) : SongRecognizer {

    override suspend fun identifySong(pcmData: ByteArray): SongResult? {
        if (apiKey == "test_api_key_here" || apiKey.isEmpty()) {
            // Mock mode to test UI transitions without burning quota
            delay(1500) // Simulate fast network request for song ID
            return SongResult("Blinding Lights", "The Weeknd", null, "00:30")
        }

        val wavData = addWavHeader(pcmData)

        try {
            val response = client.submitFormWithBinaryData(
                url = "https://api.audd.io/",
                formData = formData {
                    append("api_token", apiKey)
                    append("audio", wavData, Headers.build {
                        append(HttpHeaders.ContentType, "audio/wav")
                        append(HttpHeaders.ContentDisposition, "filename=\"sample.wav\"")
                    })
                }
            )
            val responseString = response.bodyAsText()
            
            if (responseString.contains("\"status\":\"success\"")) {
                val title = extractJsonValue(responseString, "title") ?: "Unknown Song"
                val artist = extractJsonValue(responseString, "artist") ?: "Unknown Artist"
                return SongResult(title, artist, null)
            }
        } catch (e: Exception) {
            println("AudD Error: ${e.message}")
        }
        return null
    }

    private fun extractJsonValue(json: String, key: String): String? {
        val regex = "\"$key\"\\s*:\\s*\"([^\"]+)\"".toRegex()
        val match = regex.find(json)
        return match?.groups?.get(1)?.value
    }

    private fun addWavHeader(pcmData: ByteArray): ByteArray {
        val totalDataLen = pcmData.size + 36
        val byteRate = 44100 * 2 * 1
        val header = ByteArray(44)
        
        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
        
        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0
        header[22] = 1; header[23] = 0
        
        header[24] = (44100 and 0xff).toByte()
        header[25] = ((44100 shr 8) and 0xff).toByte()
        header[26] = ((44100 shr 16) and 0xff).toByte()
        header[27] = ((44100 shr 24) and 0xff).toByte()
        
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        
        header[32] = 2; header[33] = 0
        header[34] = 16; header[35] = 0
        
        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
        header[40] = (pcmData.size and 0xff).toByte()
        header[41] = ((pcmData.size shr 8) and 0xff).toByte()
        header[42] = ((pcmData.size shr 16) and 0xff).toByte()
        header[43] = ((pcmData.size shr 24) and 0xff).toByte()
        
        return header + pcmData
    }
}
