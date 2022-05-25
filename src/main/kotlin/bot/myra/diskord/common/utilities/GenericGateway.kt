package bot.myra.diskord.common.utilities

import bot.myra.diskord.gateway.handler.OptCode
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
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
    private val waitingCalls = mutableListOf<OptCode>()

    val client = HttpClient(CIO) {
        install(WebSockets)
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
                    val income = JSON.decodeFromString<OptCode>(data.readText())
                    handleIncome(income, resumed)
                }
            } ?: throw ClosedReceiveChannelException("Couldn't open a websocket connection to $url")
        }
        // Manage disconnects ➜ try reconnecting
        catch (e: ClosedReceiveChannelException) {
            logger.info("Lost connection...")
        }
        val reasonSocket = socket
        socket = null
        reasonSocket?.let {
            val reason = withTimeoutOrNull(5.seconds) { reasonSocket.closeReason.await() }
            logger.warn("Socket closed with reason $reason - attempting reconnection")
        }
        openGatewayConnection(true)
    }

    open suspend fun onConnectionOpened(resume: Boolean) {}

    suspend fun ready() {
        // Send queued calls
        while (waitingCalls.size != 0) {
            val opcode = waitingCalls.removeFirst()
            send(opcode)
        }
    }

    abstract suspend fun handleIncome(opcode: OptCode, resume: Boolean)

    /**
     * Sends the provided [OptCode] to the websocket.
     * If the websocket isn't connected, the opt-code will get added to [waitingCalls].
     * All waiting calls get executed as soon as the websocket is connected again.
     *
     * @param opcode Opcode to send.
     */
    suspend fun send(opcode: OptCode) {
        logger.debug(">> ${opcode.toJson()}")
        socket?.send(opcode.toJson()) ?: waitingCalls.add(opcode)
    }

}