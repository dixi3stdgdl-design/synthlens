package com.synthlens.app.engine

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class AuddRequest(
    val api_token: String,
    val audio: String,
    val `return`: String
)

@Serializable
data class AuddResult(
    val title: String? = null,
    val artist: String? = null
)

@Serializable
data class AuddResponse(
    val status: String,
    val result: AuddResult? = null
)

/**
 * Cliente multiplataforma (KMP) para conectarse a la API de reconocimiento musical de AudD.io.
 * Utiliza Ktor para las peticiones HTTP y kotlinx.serialization para el JSON.
 */
object NetworkClient {

    private const val AUDD_API_URL = "https://api.audd.io/"
    // TODO: Reemplazar con la API Key real de AudD
    private const val API_TOKEN = "TEST_API_TOKEN"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    /**
     * Envía un fragmento de audio PCM a la API de AudD para reconocimiento.
     * Retorna un Pair con (Canción, Artista) o null si falla.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun recognizeAudio(pcmData: FloatArray, sampleRate: Int = 44100): Pair<String, String>? {
        // En Multiplatform podemos delegar a Default dispatcher en lugar de IO,
        // o si IO no está disponible globalmente, al Default
        return try {
            // 1. Convertir FloatArray a un WAV simple en memoria
            val wavBytes = createWavHeaderAndData(pcmData, sampleRate)
            
            // 2. Codificar en Base64 con la API Experimental de Kotlin Multiplatform
            val base64Audio = Base64.Default.encode(wavBytes)

            val payload = AuddRequest(
                api_token = API_TOKEN,
                audio = base64Audio,
                `return` = "timecode"
            )

            // 3. Hacer la petición HTTP POST
            val response: AuddResponse = httpClient.post(AUDD_API_URL) {
                contentType(ContentType.Application.Json)
                setBody(payload)
            }.body()

            if (response.status == "success" && response.result != null) {
                val title = response.result.title ?: "Unknown Title"
                val artist = response.result.artist ?: "Unknown Artist"
                Pair(title, artist)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createWavHeaderAndData(pcmData: FloatArray, sampleRate: Int): ByteArray {
        val channels = 1
        val byteRate = 16 * sampleRate * channels / 8
        val totalAudioLen = pcmData.size * 2
        val totalDataLen = totalAudioLen + 36

        // En KMP no tenemos ByteArrayOutputStream nativo de Java. Usamos un arreglo fijo.
        val out = ByteArray(totalDataLen + 8)
        var cursor = 0

        val header = ByteArray(44)
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte() // 'fmt ' chunk
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16 // 4 bytes: size of 'fmt ' chunk
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1 // format = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (2 * 16 / 8).toByte() // block align
        header[33] = 0
        header[34] = 16 // bits per sample
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (totalAudioLen and 0xff).toByte()
        header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
        header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
        header[43] = ((totalAudioLen shr 24) and 0xff).toByte()

        header.copyInto(out, cursor)
        cursor += 44

        // Escribir datos PCM (16 bit)
        for (f in pcmData) {
            val clamped = f.coerceIn(-1f, 1f)
            val sample = (clamped * Short.MAX_VALUE).toInt()
            out[cursor++] = (sample and 0xff).toByte()
            out[cursor++] = ((sample shr 8) and 0xff).toByte()
        }

        return out
    }
}
