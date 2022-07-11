package com.efremovkirill.securedchat.connection

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import java.nio.charset.StandardCharsets

private const val SERVER_IP = "10.0.2.2"

class ClientConnection private constructor(builder: Builder) {

    private var selectorManager: SelectorManager? = null
    private var sendChannel: ByteWriteChannel? = null

    var socket: Socket? = null

    init {
        socket = builder.getSocket()
        receiveChannel = socket?.openReadChannel()
        sendChannel = socket?.openWriteChannel(autoFlush = true)

        selectorManager = builder.getSelectorManager()
    }

    class Builder {
        private var socket: Socket? = null
        private var selectorManager: SelectorManager? = null

        fun setupSocket(socket: Socket) = apply { this.socket = socket }
        fun setupSelectorManager(selectorManager: SelectorManager) = apply { this.selectorManager = selectorManager }

        fun getSocket() = socket
        fun getSelectorManager() = selectorManager

        fun build() = ClientConnection(this)
    }

    suspend fun write(string: String) {
        try {
            sendChannel?.writeStringUtf8(string)
        } catch (_: Exception) {
        }
    }

    suspend fun receive(): String {
        var receivedMessage: String = ""
        receiveChannel?.read { receivedMessage = StandardCharsets.UTF_8.decode(it).toString() }

        while (receivedMessage == "") delay(100)

        return receivedMessage
    }

    fun getReceiveChannel() = receiveChannel

    suspend fun reconnect(): ClientConnection {
        closeConnection()
        return getClientConnection()
    }

    fun closeConnection() {
        socket?.close()
        selectorManager?.close()
    }

    companion object {
        @Volatile
        private var INSTANCE: ClientConnection? = null

        @Volatile
        var receiveChannel: ByteReadChannel? = null

        suspend fun getClientConnection(): ClientConnection {
            val tempInstance = INSTANCE
            if (tempInstance != null && tempInstance.socket?.isClosed == false) return tempInstance

            val selectorManager = SelectorManager(Dispatchers.IO)
            val socket = aSocket(selectorManager).tcp().connect(hostname = SERVER_IP, port = 33482)
            val instance = Builder()
                .setupSocket(socket)
                .setupSelectorManager(selectorManager)
                .build()

            INSTANCE = instance
            return instance
        }

        suspend fun reconnect(): ClientConnection {
            INSTANCE = null
            return getClientConnection()
        }
    }
}