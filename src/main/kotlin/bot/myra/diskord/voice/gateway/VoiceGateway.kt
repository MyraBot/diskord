package bot.myra.diskord.voice.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.events.impl.*
import bot.myra.diskord.voice.gateway.models.Operations
import bot.myra.diskord.voice.gateway.models.VoiceGatewayCloseReason
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import org.slf4j.LoggerFactory
import kotlin.reflect.full.findAnnotation
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class VoiceGateway(
    val scope: CoroutineScope,
    endpoint: String,
    internal val token: String,
    internal val session: String,
    internal val guildId: String,
    val diskord: Diskord
) : GenericGateway(LoggerFactory.getLogger(VoiceGateway::class.java), scope) {
    val url: String = "wss://$endpoint/?v=4"
    val eventDispatcher = MutableSharedFlow<GenericVoiceEvent>()
    internal var lastTimestamp: Long = System.currentTimeMillis()
    val ssrcToUserId = mutableMapOf<UInt, String>()

    init {
        scope.launch {
            val deserializer = Json { ignoreUnknownKeys = true }
            incomingEvents.mapNotNull {
                val payload = it.d ?: throw IllegalStateException("Invalid voice payload")
                when (Operations.from(it.op)) {
                    Operations.IDENTIFY              -> TODO("identify")
                    Operations.SELECT_PROTOCOL       -> TODO("select protocol")
                    Operations.READY                 -> VoiceReadyEvent.deserialize(deserializer, this@VoiceGateway, payload)
                    Operations.HEARTBEAT             -> TODO("heartbeat")
                    Operations.SESSION_DESCRIPTION   -> VoiceSessionDescriptionEvent.deserialize(deserializer, this@VoiceGateway, payload)
                    Operations.SPEAKING              -> VoiceSpeakingEvent.deserialize(deserializer, this@VoiceGateway, payload)
                    Operations.HEARTBEAT_ACKNOWLEDGE -> VoiceHeartbeatAcknowledgeEvent.deserialize(deserializer, this@VoiceGateway, payload)
                    Operations.RESUME                -> TODO("resume")
                    Operations.HELLO                 -> VoiceHelloEvent.deserialize(deserializer, this@VoiceGateway, payload)
                    Operations.RESUMED               -> TODO("resumed")
                    Operations.CLIENT_DISCONNECT     -> TODO("client disconnect")
                    Operations.USER_DISCONNECT       -> TODO("user disconnect")
                    Operations.USER_CONNECT          -> TODO("User connect")
                    Operations.INVALID               -> {
                        logger.debug("Received unknown voice opcode: " + it.op)
                        null
                    }
                }
            }.collect { eventDispatcher.emit(it) }
        }
    }

    suspend inline fun <reified T : GenericVoiceEvent> awaitEvent(timeout: Duration = 5.seconds): T? {
        val operation = T::class.findAnnotation<VoiceEvent>()?.operation ?: return null
        return withTimeoutOrNull(timeout) {
            eventDispatcher.first { it.operation == operation } as T?
        }
    }

    fun connect(resume: Boolean = false) = openSocketConnection(url, resume)

    suspend fun disconnect() {
        socket?.close()
        socket = null
        logger.debug("Disconnected from socket")
    }

    override suspend fun handleClose(reason: CloseReason?) {
        val closeReason = VoiceGatewayCloseReason.fromCode(reason?.code)

        if (closeReason == null) {
            super.state = GatewayState.STOPPED
            return
        }

        if (closeReason.reconnect) {
            super.state = GatewayState.RECONNECTING
            connect(true)
            return
        }

        super.state = GatewayState.STOPPED
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