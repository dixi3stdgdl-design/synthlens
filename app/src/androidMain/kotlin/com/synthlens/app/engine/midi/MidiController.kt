package com.synthlens.app.engine.midi

import android.content.Context
import android.media.midi.MidiDeviceInfo
import android.media.midi.MidiManager

class MidiController(private val context: Context) {
    private val midiManager: MidiManager? = context.getSystemService(Context.MIDI_SERVICE) as MidiManager?

    fun getConnectedDevices(): List<MidiDeviceInfo> {
        return midiManager?.devices?.toList() ?: emptyList()
    }

    fun pushPatchToDevice(deviceInfo: MidiDeviceInfo, synthName: String, params: Map<String, Float>) {
        midiManager?.openDevice(deviceInfo, { device ->
            val outputPort = device?.openInputPort(0)
            if (outputPort != null) {
                try {
                    // Send universal SysEx header
                    val sysexHeader = byteArrayOf(0xF0.toByte(), 0x7E.toByte(), 0x7F.toByte(), 0x06.toByte(), 0x01.toByte(), 0xF7.toByte())
                    outputPort.send(sysexHeader, 0, sysexHeader.size)

                    // Map specific parameters to Control Change (CC) messages
                    // Format: [Status (0xB0 for Ch 1), CC Number, Value (0-127)]
                    params.forEach { (key, value) ->
                        val ccNumber = when (key.lowercase()) {
                            "cutoff" -> 74
                            "resonance" -> 71
                            "attack" -> 73
                            "release" -> 72
                            "volume" -> 7
                            "pan" -> 10
                            else -> -1
                        }

                        if (ccNumber != -1) {
                            // Scale normalized value (0.0 - 1.0) to MIDI range (0 - 127)
                            val midiValue = (value.coerceIn(0f, 1f) * 127).toInt().toByte()
                            val ccMessage = byteArrayOf(0xB0.toByte(), ccNumber.toByte(), midiValue)
                            outputPort.send(ccMessage, 0, ccMessage.size)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    outputPort.close()
                }
            }
        }, null)
    }
}
