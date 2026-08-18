package com.synthlens.app.engine.daw

import android.content.Context
import android.net.Uri
import com.synthlens.app.engine.AudioAnalysis
import com.synthlens.app.engine.DetectedSynthResult
import java.io.OutputStreamWriter

class DAWExportService(private val context: Context) {
    fun exportToAbletonXML(uri: Uri, profile: DetectedSynthResult?, analysis: AudioAnalysis?) {
        try {
            context.contentResolver.openOutputStream(uri)?.use { os ->
                val writer = OutputStreamWriter(os)
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                writer.write("<Ableton MajorVersion=\"5\" MinorVersion=\"11.0_11300\" SchemaChangeCount=\"3\">\n")
                writer.write("  <Patch>\n")
                writer.write("    <Name Value=\"${profile?.name ?: "Unknown Synth"}\" />\n")
                writer.write("    <DeviceType Value=\"${profile?.category ?: "Synth"}\" />\n")
                writer.write("    <DetectedFrequency Value=\"${analysis?.frequency ?: 440.0}\" />\n")
                writer.write("    <DetectedWaveform Value=\"${analysis?.waveformType ?: "sine"}\" />\n")
                writer.write("  </Patch>\n")
                writer.write("</Ableton>\n")
                writer.flush()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
