package bot.myra.diskord.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.ReconnectMethod
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.commands.GatewayCommand
import bot.myra.diskord.gateway.commands.GatewayResume
import bot.myra.diskord.gateway.commands.IdentifyResponse
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.events.Events
import bot.myra.kommons.debug
import bot.myra.kommons.info
import bot.myra.kommons.kInfo
import bot.myra.kommons.trace
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import org.slf4j.LoggerFactory
import java.util.concurrent.ForkJoinPool

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
class Gateway(
    private val intents: MutableSet<GatewayIntent> = mutableSetOf(),
) : GenericGateway(LoggerFactory.getLogger(Gateway::class.java)) {
    private val coroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher() + CoroutineName("Websocket"))
    lateinit var session: String
    override val url: String = "wss://gateway.discord.gg/?v=9&encoding=json"
    override var resumeUrl: String? = null
    private var sequence: Int = 0
    val eventDispatcher = MutableSharedFlow<OpPacket>()

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Starts the event resolver and opens a gateway websocket connection.
     */
    suspend fun connect() {
        Events.startResolver()
        coroutineScope.launch { openGatewayConnection() }
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#identifying)
     *
     * Identifies the bot to the websocket server, gets send after receiving the ready event.
     * With this the bot loads its required intents.
     */
    private suspend fun identify() {
        kInfo(this::class) { "Logging in with intents of $intents (${GatewayIntent.getID(intents)})" }
        send(IdentifyResponse(Diskord.token, GatewayIntent.getID(intents), IdentifyResponse.Properties()))
    }

    override suspend fun chooseReconnectMethod(reason: CloseReason): ReconnectMethod = when (GatewaySocketClosedReason.fromCode(reason.code)) {
        GatewaySocketClosedReason.UNKNOWN_ERROR         -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.UNKNOWN_OPCODE        -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.DECODE_ERROR          -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.NOT_AUTHENTICATED     -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.AUTHENTICATION_FAILED -> ReconnectMethod.STOP
        GatewaySocketClosedReason.ALREADY_AUTHENTICATED -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.INVALID_SEQUENCE      -> ReconnectMethod.CONNECT
        GatewaySocketClosedReason.RATE_LIMITED          -> ReconnectMethod.RETRY
        GatewaySocketClosedReason.SESSION_TIMED_OUT     -> ReconnectMethod.CONNECT
        GatewaySocketClosedReason.INVALID_SHARD         -> throw Exception("Invalid shard")
        GatewaySocketClosedReason.SHARDING_REQUIRED     -> ReconnectMethod.STOP
        GatewaySocketClosedReason.INVALID_API_VERSION   -> throw Exception("Invalid gateway version")
        GatewaySocketClosedReason.INVALID_INTENTS       -> ReconnectMethod.STOP
        GatewaySocketClosedReason.DISALLOWED_INTENTS    -> throw Exception("You may have tried to specify an intent that you have not enabled or are not approved for.")
        GatewaySocketClosedReason.UNKNOWN               -> ReconnectMethod.RETRY
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#resuming)
     * Resumes an old websocket connection.
     */
    private suspend fun resume() {
        send(GatewayResume(Diskord.token, session, sequence))
    }

    override suspend fun handleIncome(packet: OpPacket, resumed: Boolean) {
        when (OpCode.from(packet.op)) {
            OpCode.DISPATCH              -> fireEvent(packet)
            OpCode.HEARTBEAT             -> sendHeartbeat().also { debug(this::class) { "Received Heartbeat attack!" } }
            OpCode.RECONNECT             -> {
                logger.info("Received reconnect from discord -> closing connection")
                throw ClosedReceiveChannelException("Received reconnect from discord")
            }
            OpCode.HELLO                 -> hello(packet, resumed)
            OpCode.HEARTBEAT_ACKNOWLEDGE -> debug(this::class) { "Heartbeat acknowledged!" }
        }
    }

    private suspend fun fireEvent(packet: OpPacket) {
        eventDispatcher.emit(packet)
        sequence = packet.s ?: sequence
    }

    private suspend fun hello(packet: OpPacket, resumed: Boolean) {
        startHeartbeat(packet)
        if (resumed) resume()
        else identify()
        info(this::class) { "Successfully connected to Discord" }
        ready()
    }

    /**
     * Starts the heartbeat loop.
     *
     * @param income The received [OpPacket].
     */
    private fun startHeartbeat(income: OpPacket) {
        val heartbeatInterval = income.d!!.jsonObject["heartbeat_interval"]!!.jsonPrimitive.long
        coroutineScope.launch {
            while (isActive) {
                sendHeartbeat()
                delay(heartbeatInterval)
            }
        }
    }

    /**
     * Sends a heartbeat response to Discord.
     */
    private suspend fun sendHeartbeat() {
        // Using the second send function to avoid queueing the call if the socket is null
        socket?.send {
            op = OpCode.HEARTBEAT.code
            s = sequence
        }
        trace(this::class) { "Sent heartbeat!" }
    }

    /**
     * Updates the applications presence and status.
     * **Is **
     *
     * @param presence New presence / status.
     */
    suspend fun updatePresence(presence: PresenceUpdate) = send(presence)

    private suspend inline fun <reified T : GatewayCommand> send(command: T) = send {
        op = command.operation?.code ?: throw Exception("No opcode provided by operation ${T::class.simpleName}")
        d = command.toJsonObj(true)
    }

}

/**
 * Registers all registered events from [Diskord.listenersPackage],
 * initialises [Diskord] and starts the websocket.
 *
 * @return Returns the [Diskord] object. Just for laziness.
 */
suspend fun Diskord.connectGateway() {
    info(this::class) { "Registering discord event listeners" }
    listenersPackage.forEach { Events.findListeners(it) }

    if (hasWebsocketConnection()) throw Exception("The websocket is already connected")
    val ws = Gateway(this.intents) // Create websocket
    Diskord.apply { gateway = ws }
    ws.connect() // Open websocket connection
}