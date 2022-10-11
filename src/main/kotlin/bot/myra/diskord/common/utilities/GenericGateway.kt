package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.OpPacket
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.serialization.decodeFromString
import org.slf4j.Logger
import java.io.EOFException
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

    private val client = HttpClient(OkHttp) {
        install(WebSockets) { pingInterval = 20.seconds.inWholeMilliseconds }
        install(HttpTimeout) { requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS }
        expectSuccess = true
    }

    var socket: DefaultClientWebSocketSession? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val connected get() = socket != null && !socket!!.outgoing.isClosedForSend && !socket!!.incoming.isClosedForReceive
    var resumedConnection = false

    val incomingEvents = MutableSharedFlow<OpPacket>()

    fun openSocketConnection(url: String, resume: Boolean) {
        coroutineScope.launch {
            // Make sure no other socket runs
            withTimeout(5.seconds) {
                while (connected) delay(500)
            }

            resumedConnection = resume
            if (resume) logger.info("Resuming gateway connection")
            else logger.info("Connecting to gateway")

            socket = client.webSocketSession(url) { expectSuccess = false }

            socket?.apply {
                logger.debug("Opened websocket connection to $url")

                try {
                    handleIncome()
                } catch (_: EOFException) {
                    logger.info("End of income")
                }

                val reason = withTimeoutOrNull(3.seconds) { closeReason.await() }
                if (reason != null) logger.warn("Socket ended with reason ${reason.message} (${reason.code})")
                else logger.warn("Socket ended with no reason")
                handleClose(reason)
            }

        }
    }

    suspend fun stop() {
        socket?.close(CloseReason(1000, "Closed by user"))
    }

    /**
     * Handles incoming data
     */
    private suspend fun DefaultClientWebSocketSession.handleIncome() {
        this.incoming.consumeAsFlow().buffer(Channel.UNLIMITED).collect {
            val data = it as Frame.Text
            logger.debug("<< ${data.readText()}")
            val packet = JSON.decodeFromString<OpPacket>(data.readText())
            incomingEvents.emit(packet)
        }
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