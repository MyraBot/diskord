package bot.myra.diskord.voice.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.handler.OptCode
import bot.myra.diskord.voice.gateway.commands.Identify
import bot.myra.diskord.voice.gateway.commands.Resume
import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import bot.myra.diskord.voice.gateway.models.HelloPayload
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.slf4j.LoggerFactory

class VoiceGateway(
    private val endpoint: String,
    private val token: String,
    private val session: String,
    private val guildId: String
) : GenericGateway(
    url = "wss://$endpoint/?v=4",
    logger = LoggerFactory.getLogger(VoiceGateway::class.java)
) {
    private val scope = CoroutineScope(Dispatchers.Default)
    val eventDispatcher = MutableSharedFlow<OptCode>()
    private var lastTimestamp: Long = System.currentTimeMillis()

    suspend fun connect() = scope.launch {
        openGatewayConnection()
    }

    override suspend fun handleIncome(opcode: OptCode, resume: Boolean) {
        val op = opcode.op
        when (Operations.from(op)) {
            Operations.READY                 -> eventDispatcher.emit(opcode)
            Operations.SESSION_DESCRIPTION   -> eventDispatcher.emit(opcode)
            Operations.HEARTBEAT_ACKNOWLEDGE -> handleHeartbeat(opcode)
            Operations.HELLO                 -> startHeartbeat(opcode)
            //Operations.RESUMED               -> TODO()
            //Operations.CLIENT_DISCONNECT     -> TODO()
            Operations.INVALID               -> throw Exception()
        }
    }

    override suspend fun onConnectionOpened(resumed: Boolean) {
        if (resumed) send(Resume(guildId, session, token))
        else send(Identify(guildId, Diskord.id, session, token))
    }

    private fun startHeartbeat(hello: OptCode) = scope.launch {
        val interval = hello.d?.let { JSON.decodeFromJsonElement<HelloPayload>(it) }?.heartbeatInterval?.toLong() ?: throw IllegalStateException("Invalid hello payload")

        eventDispatcher.first { it.op == Operations.READY.code }
        val timestamp: Long = System.currentTimeMillis()

        while (true) {
            lastTimestamp = System.currentTimeMillis() - timestamp
            send(OptCode(op = Operations.HEARTBEAT.code, d = JsonPrimitive(lastTimestamp), s = null, t = null))
            delay(interval)
        }
    }

    private fun handleHeartbeat(opcode: OptCode) {
        if (opcode.d?.jsonPrimitive?.long != lastTimestamp) logger.warn("Received non matching heartbeat")
        else logger.debug("Acknowledged heartbeat")
    }

    /**
     * Sends a command to the voice gateway.
     *
     * @param command The command to send.
     */
    internal suspend inline fun <reified T : VoiceCommand> send(command: T) = send(OptCode(
        op = command.operation.code,
        d = JSON.encodeToJsonElement(command),
        s = null,
        t = null
    ))

}