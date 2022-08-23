package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.OpPacket
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import java.io.EOFException
import java.net.SocketException
import kotlin.time.Duration.Companion.seconds

abstract class GenericGateway(val logger: Logger) {
    abstract val url: String
    open var resumeUrl: String? = null

    /**
     * Queued calls which couldn't be delivered to Discord because
     * of no connection.
     * This mostly happened when trying to send an opcode while the
     * websocket isn't connected.
     */
    private val waitingCalls = mutableListOf<OpPacket>()

    val client = HttpClient(OkHttp) {
        install(WebSockets)
        install(HttpTimeout) { requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS }
        expectSuccess = true
    }

    var socket: DefaultClientWebSocketSession? = null
    val connected get() = socket != null

    suspend fun openGatewayConnection(resumed: Boolean = false) {
        if (resumed) logger.info("Reconnecting...")
        try {
            startConnection(resumed)
        } catch (e: EOFException) {
            openGatewayConnection(resumed)
        }
    }

    private suspend fun startConnection(resumed: Boolean) {
        val socketUrl = if (resumed) resumeUrl ?: url else url
        socket = client.webSocketSession {
            url(socketUrl)
            expectSuccess = false
        }
        socket?.apply {
            logger.info("Opened websocket connection to $socketUrl")
            // Handle incoming data
            try {
                collectIncoming(resumed)
            } catch (e: Exception) { // On disconnect
                logger.warn("Connection got disconnected: ${e.message}")
            }
            logger.debug("Lost connection")
            socket?.apply { handleDisconnect(this) } ?: openGatewayConnection(false)
        } ?: throw ClosedReceiveChannelException("Couldn't open a websocket connection to $url")
    }

    private suspend fun DefaultClientWebSocketSession.collectIncoming(resumed: Boolean) {
        incoming.receiveAsFlow().collect {
            val data = it as Frame.Text
            logger.debug("<< ${data.readText()}")
            val income = JSON.decodeFromString<OpPacket>(data.readText())
            handleIncome(income, resumed)
        }
    }

    private suspend fun handleDisconnect(socket: DefaultClientWebSocketSession) {
        socket.close()
        val reason: CloseReason = withTimeoutOrNull(3.seconds) {
            try {
                return@withTimeoutOrNull socket.closeReason.await()
            } catch (e: Exception) {
                when (e) {
                    is TimeoutCancellationException -> Unit // Thrown when the #withTimeoutOrNull cancels the closeReason#await function
                    is SocketException              -> return@withTimeoutOrNull CloseReason(4000, e.message ?: "Socket exception")
                    is EOFException                 -> return@withTimeoutOrNull CloseReason(4000, e.message ?: "EOF exception")
                    else                            -> e.cause?.printStackTrace()
                }
            }
            null
        } ?: return openGatewayConnection(true)

        logger.warn("Socket closed with reason ${reason.message} (${reason.code})")
        if (reason.knownReason == null) checkSocketSpecificReasons(reason)
        else openGatewayConnection(true)
    }

    private suspend fun checkSocketSpecificReasons(reason: CloseReason) = when (chooseReconnectMethod(reason)) {
        ReconnectMethod.CONNECT -> openGatewayConnection(false)
        ReconnectMethod.RETRY   -> openGatewayConnection(true)
        ReconnectMethod.STOP    -> Unit
    }

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
        socket?.send(packet.toJson(true))?.also {
            logger.debug(">> ${packet.toJson()}")
        } ?: waitingCalls.add(packet)
    }

    suspend fun send(builder: OpPacket.() -> Unit) = send(OpPacket(op = -1).apply(builder))

    /**
     * Sends an opcode to a non-nullable websocket connection. With this, calls don't get queued.
     *
     * @param builder An opcode packet builder to easily create a packet.
     */
    suspend fun DefaultClientWebSocketSession.send(builder: OpPacket.() -> Unit) = send(OpPacket(op = -1).apply(builder))

}