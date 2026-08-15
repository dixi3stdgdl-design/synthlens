package com.synthlens.app.engine.daw

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer

class OscSender(private val ipAddress: String, private val port: Int) {
    
    suspend fun sendMessage(address: String, vararg args: Any) = withContext(Dispatchers.IO) {
        try {
            DatagramSocket().use { socket ->
                val buffer = encodeOscMessage(address, args.toList())
                val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName(ipAddress), port)
                socket.send(packet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun encodeOscMessage(address: String, args: List<Any>): ByteArray {
        val addressBytes = address.toByteArray()
        val addressPadded = addressBytes.size + 4 - (addressBytes.size % 4)
        
        var typeString = ","
        val argBytesList = mutableListOf<ByteArray>()
        
        for (arg in args) {
            when (arg) {
                is Float -> {
                    typeString += "f"
                    argBytesList.add(ByteBuffer.allocate(4).putFloat(arg).array())
                }
                is Int -> {
                    typeString += "i"
                    argBytesList.add(ByteBuffer.allocate(4).putInt(arg).array())
                }
                is String -> {
                    typeString += "s"
                    val b = arg.toByteArray()
                    val p = b.size + 4 - (b.size % 4)
                    val out = ByteArray(p)
                    System.arraycopy(b, 0, out, 0, b.size)
                    argBytesList.add(out)
                }
            }
        }
        
        val typeBytes = typeString.toByteArray()
        val typePadded = typeBytes.size + 4 - (typeBytes.size % 4)
        
        val totalSize = addressPadded + typePadded + argBytesList.sumOf { it.size }
        val buffer = ByteBuffer.allocate(totalSize)
        
        val aBytes = ByteArray(addressPadded)
        System.arraycopy(addressBytes, 0, aBytes, 0, addressBytes.size)
        buffer.put(aBytes)
        
        val tBytes = ByteArray(typePadded)
        System.arraycopy(typeBytes, 0, tBytes, 0, typeBytes.size)
        buffer.put(tBytes)
        
        for (b in argBytesList) {
            buffer.put(b)
        }
        
        return buffer.array()
    }
}
