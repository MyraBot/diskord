package bot.myra.diskord.voice.gateway

import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import bot.myra.diskord.voice.gateway.handlers.VoiceHeartbeatAcknowledgeEventHandler
import bot.myra.diskord.voice.gateway.handlers.VoiceHelloEventHandler
import bot.myra.diskord.voice.gateway.handlers.VoiceReadyEventHandler
import bot.myra.diskord.voice.gateway.handlers.VoiceSessionDescriptionEventHandler
import bot.myra.diskord.voice.gateway.models.VoiceGatewayCloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.LoggerFactory

class VoiceGateway(
    internal val scope: CoroutineScope,
    endpoint: String,
    internal val token: String,
    internal val session: String,
    internal val guildId: String
) : GenericGateway(LoggerFactory.getLogger(VoiceGateway::class.java), scope) {
    val url: String = "wss://$endpoint/?v=4"
    val eventDispatcher = MutableSharedFlow<OpPacket>()
    internal var lastTimestamp: Long = System.currentTimeMillis()

    init {
        VoiceHeartbeatAcknowledgeEventHandler(this).listen()
        VoiceHelloEventHandler(this).listen()
        VoiceReadyEventHandler(this).listen()
        VoiceSessionDescriptionEventHandler(this).listen()
    }

    fun connect(resume: Boolean = false) = openSocketConnection(url, resume)

    suspend fun disconnect() {
        socket?.close()
        socket = null
        logger.debug("Disconnected from socket")
    }

    override fun handleClose(reason: CloseReason?) {
        val closeReason = VoiceGatewayCloseReason.fromCode(reason?.code)
        if (closeReason.reconnect) connect(true)
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