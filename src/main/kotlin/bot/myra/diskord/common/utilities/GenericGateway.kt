package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.GatewayClosedReason
import bot.myra.diskord.gateway.OpPacket
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import java.io.EOFException
import java.net.SocketException
import kotlin.time.Duration.Companion.seconds

abstract class GenericGateway(
    val logger: Logger,
    internal open val coroutineScope: CoroutineScope
) {

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
    var resume = false

    val incomingEvents = MutableSharedFlow<OpPacket>()

    fun openSocketConnection(url: String, resume: Boolean) {
        //TODO
        /*val socketExceptionHandler = CoroutineExceptionHandler { _, exception ->
            if (exception is EOFException) openSocketConnection(url)
        }*/
        this.resume = resume

        // coroutineScope.launch(socketExceptionHandler) {
        coroutineScope.launch {
            socket = client.webSocketSession(url) { expectSuccess = false }
            socket?.apply {
                logger.debug("Opened websocket connection to $url")

                handleIncome()

                val reason = getCloseReason()
                if (reason != null) logger.warn("Socket ended with reason ${reason.message} (${reason.code})")
                else logger.warn("Socket ended with no reason")
                handleClose(reason)
            }
        }
    }

    suspend fun closeSocketConnection(reason: GatewayClosedReason) {
        val closeReason = CloseReason(reason.code ?: 1000, reason.cause)
        socket?.close(closeReason)
    }

    /**
     * Handles incoming data
     */
    private suspend fun DefaultClientWebSocketSession.handleIncome() {
        try {
            this.incoming.consumeAsFlow().buffer(Channel.UNLIMITED).collect {
                val data = it as Frame.Text
                logger.debug("<< ${data.readText()}")
                val packet = JSON.decodeFromString<OpPacket>(data.readText())
                incomingEvents.emit(packet)
            }
        } catch (e: Exception) { // On disconnect
            logger.warn("Lost connection when receiving incoming data: ${e.message} (${e::class.simpleName})")
        }
    }

    private suspend fun DefaultClientWebSocketSession.getCloseReason(): CloseReason? {
        val reason: CloseReason? = withTimeoutOrNull(3.seconds) {
            try {
                return@withTimeoutOrNull closeReason.await()
            } catch (e: Exception) {
                when (e) {
                    is TimeoutCancellationException -> null // Thrown when the #withTimeoutOrNull cancels the closeReason#await function
                    is SocketException              -> return@withTimeoutOrNull CloseReason(1000, e.message ?: "Socket exception")
                    is EOFException                 -> return@withTimeoutOrNull CloseReason(1000, e.message ?: "EOF exception")
                    else                            -> throw e.cause ?: Exception("Unknown exception")
                }
            }
        }
        socket = null
        return reason
    }

    abstract fun handleClose(reason: CloseReason?)

    suspend fun sendQueuedCalls() {
        // Send queued calls
        while (waitingCalls.size != 0) {
            val opcode = waitingCalls.removeFirst()
            send(opcode)
        }
    }

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
    @Suppress("UnusedReceiverParameter")
    suspend fun DefaultClientWebSocketSession.send(builder: OpPacket.() -> Unit) = send(OpPacket(op = -1).apply(builder))

}