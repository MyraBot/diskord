package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.OpPacket
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import kotlin.time.Duration.Companion.seconds

abstract class GenericGateway(
    open val url: String,
    val logger: Logger
) {

    /**
     * Queued calls which couldn't be delivered to Discord because
     * of no connection.
     * This mostly happened when trying to send an opcode while the
     * websocket isn't connected.
     */
    private val waitingCalls = mutableListOf<OpPacket>()

    val client = HttpClient(CIO) {
        install(WebSockets)
        install(HttpTimeout) { requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS }
        expectSuccess = true
    }

    var socket: DefaultClientWebSocketSession? = null
    val connected get() = socket != null

    suspend fun openGatewayConnection(resumed: Boolean = false) {
        try {
            socket = client.webSocketSession(url)
            socket?.apply {
                logger.info("Opened websocket connection")
                onConnectionOpened(resumed)

                // Handle incoming data
                incoming.receiveAsFlow().collect {
                    val data = it as Frame.Text
                    logger.debug("<< ${data.readText()}")
                    val income = JSON.decodeFromString<OpPacket>(data.readText())
                    handleIncome(income, resumed)
                }
                logger.debug("Reached end of socket")
            } ?: throw ClosedReceiveChannelException("Couldn't open a websocket connection to $url")
        }
        // On disconnect
        catch (e: Exception) {
            logger.warn("Lost connection: ${e.message}")
            handleDisconnect()
        }
    }

    private suspend fun handleDisconnect() {
        val reasonSocket = socket ?: return
        socket = null
        val reason = withTimeoutOrNull(5.seconds) {
            reasonSocket.closeReason.await()
        }?.also { logger.warn("Socket closed with reason $it") }

        if (reason == null) openGatewayConnection(true)
        else {
            println("Chose ${chooseReconnectMethod(reason).name} method because of reason $reason")
            when (chooseReconnectMethod(reason)) {
                ReconnectMethod.CONNECT -> openGatewayConnection(false)
                ReconnectMethod.RETRY   -> openGatewayConnection(true)
                ReconnectMethod.STOP    -> return
            }
        }
    }

    open suspend fun onConnectionOpened(resumed: Boolean) {}

    open suspend fun chooseReconnectMethod(reason: CloseReason): ReconnectMethod = ReconnectMethod.RETRY

    suspend fun ready() {
        // Send queued calls
        while (waitingCalls.size != 0) {
            val opcode = waitingCalls.removeFirst()
            send(opcode)
        }
    }

    abstract suspend fun handleIncome(packet: OpPacket, resumed: Boolean)

    /**
     * Sends the provided [OpPacket] to the websocket.
     * If the websocket isn't connected, the opt-code will get added to [waitingCalls].
     * All waiting calls get executed as soon as the websocket is connected again.
     *
     * @param packet Opcode to send.
     */
    suspend fun send(packet: OpPacket) {
        logger.debug(">> ${packet.toJson()}")
        socket?.send(packet.toJson(true)) ?: waitingCalls.add(packet)
    }

    suspend fun send(builder: OpPacket.() -> Unit) = send(OpPacket(op = -1).apply(builder))

    /**
     * Sends an opcode to a non-nullable websocket connection. With this, calls don't get queued.
     *
     * @param builder An opcode packet builder to easily create a packet.
     */
    suspend fun DefaultClientWebSocketSession.send(builder: OpPacket.() -> Unit) = send(OpPacket(op = -1).apply(builder))

}

