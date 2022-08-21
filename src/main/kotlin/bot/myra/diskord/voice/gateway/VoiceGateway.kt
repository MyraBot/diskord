package bot.myra.diskord.voice.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.ReconnectMethod
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.commands.Identify
import bot.myra.diskord.voice.gateway.commands.Resume
import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import bot.myra.diskord.voice.gateway.models.HelloPayload
import bot.myra.diskord.voice.gateway.models.Operations
import bot.myra.diskord.voice.gateway.models.VoiceSocketClosedReason
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

class VoiceGateway(
    private val scope: CoroutineScope,
    endpoint: String,
    private val token: String,
    private val session: String,
    private val guildId: String
) : GenericGateway(LoggerFactory.getLogger(VoiceGateway::class.java)) {
    override val url: String = "wss://$endpoint/?v=4"
    val eventDispatcher = MutableSharedFlow<OpPacket>()
    private var lastTimestamp: Long = System.currentTimeMillis()

    suspend fun connect() = scope.launch { openGatewayConnection() }

    suspend fun disconnect() {
        socket?.close()
        socket = null
        logger.debug("Disconnected from socket")
    }

    override suspend fun handleIncome(packet: OpPacket, resumed: Boolean) {
        val op = packet.op
        when (Operations.from(op)) {
            Operations.READY                 -> eventDispatcher.emit(packet)
            Operations.SESSION_DESCRIPTION   -> eventDispatcher.emit(packet)
            Operations.HEARTBEAT_ACKNOWLEDGE -> handleHeartbeat(packet)
            Operations.HELLO                 -> onSuccessfulConnection(packet, resumed)
            //Operations.RESUMED               -> TODO()
            //Operations.CLIENT_DISCONNECT     -> TODO()
            Operations.INVALID               -> throw Exception()
        }
    }

    /**
     * Runs when the client received the acknowledgment from discord ([Operations.HELLO]).
     * Does identification and starts the heartbeat.
     *
     * @param packet The received opcode packet.
     * @param resumed Whether this is a resumed connection.
     */
    private suspend fun onSuccessfulConnection(packet: OpPacket, resumed: Boolean) {
        if (resumed) send(Resume(guildId, session, token))
        else send(Identify(guildId, Diskord.id, session, token))

        startHeartbeat(packet)
    }

    private fun startHeartbeat(hello: OpPacket) = scope.launch {
        val interval = hello.d?.let { JSON.decodeFromJsonElement<HelloPayload>(it) }?.heartbeatInterval?.toLong() ?: throw IllegalStateException("Invalid hello payload")

        eventDispatcher.first { it.op == Operations.READY.code }
        val timestamp: Long = System.currentTimeMillis()

        while (true) {
            lastTimestamp = System.currentTimeMillis() - timestamp
            send {
                op = Operations.HEARTBEAT.code
                d = JsonPrimitive(lastTimestamp)
            }
            delay(interval)
        }
    }

    private fun handleHeartbeat(packet: OpPacket) {
        if (packet.d?.jsonPrimitive?.long != lastTimestamp) logger.warn("Received non matching heartbeat")
        else logger.debug("Acknowledged heartbeat")
    }

    override suspend fun chooseReconnectMethod(reason: CloseReason): ReconnectMethod = when (VoiceSocketClosedReason.fromCode(reason.code)) {
        VoiceSocketClosedReason.UNKNOWN_OPCODE          -> ReconnectMethod.STOP
        VoiceSocketClosedReason.FAILED_PAYLOAD_DECODING -> ReconnectMethod.RETRY
        VoiceSocketClosedReason.NOT_AUTHENTICATED       -> ReconnectMethod.CONNECT
        VoiceSocketClosedReason.AUTHENTICATION_FAILED   -> ReconnectMethod.CONNECT
        VoiceSocketClosedReason.ALREADY_AUTHENTICATED   -> ReconnectMethod.STOP
        VoiceSocketClosedReason.SESSION_INVALID         -> ReconnectMethod.STOP
        VoiceSocketClosedReason.SESSION_TIMEOUT         -> ReconnectMethod.CONNECT
        VoiceSocketClosedReason.SERVER_NOT_FOUND        -> ReconnectMethod.CONNECT
        VoiceSocketClosedReason.UNKNOWN_PROTOCOL        -> ReconnectMethod.STOP
        VoiceSocketClosedReason.DISCONNECTED            -> ReconnectMethod.STOP
        VoiceSocketClosedReason.VOICE_SERVER_CRASHED    -> ReconnectMethod.RETRY
        VoiceSocketClosedReason.UNKNOWN_ENCRYPTION_MODE -> ReconnectMethod.STOP
        VoiceSocketClosedReason.UNKNOWN                 -> ReconnectMethod.RETRY
    }

    /**
     * Sends a command to the voice gateway.
     *
     * @param command The command to send.
     */
    internal suspend inline fun <reified T : VoiceCommand> send(command: T) = send {
        op = command.operation.code
        d = JSON.encodeToJsonElement(command)
    }

}