package bot.myra.diskord.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.commands.GatewayCommand
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.events.Events
import bot.myra.diskord.gateway.handlers.*
import bot.myra.kommons.info
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import org.slf4j.LoggerFactory
import java.util.concurrent.ForkJoinPool

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
class Gateway(
    internal val intents: MutableSet<GatewayIntent> = mutableSetOf(),
    override val coroutineScope: CoroutineScope = CoroutineScope(ForkJoinPool.commonPool().asCoroutineDispatcher() + CoroutineName("Gateway"))
) : GenericGateway(LoggerFactory.getLogger(Gateway::class.java), coroutineScope) {
    lateinit var session: String
    internal val url: String = "wss://gateway.discord.gg/?v=10&encoding=json"
    internal var resumeUrl: String? = null
    internal var sequence: Int = 0

    /**
     * Flow for received events. Useful for awaiting events.
     * Receives events from []
     */
    val eventFlow = MutableSharedFlow<OpPacket>()

    init {
        DispatchEventHandler(this).listen()
        HeartbeatAcknowledgeEventHandler(this).listen()
        HeartbeatEventHandler(this).listen()
        HelloEventHandler(this).listen()
        InvalidSessionHandler(this).listen()
        ReconnectEventHandler(this).listen()
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Opens the initial gateway connection.
     * Starts all event handlers and opens a gateway websocket connection.
     */
    fun connect(socketUrl: String = url, resume: Boolean = false) = openSocketConnection(socketUrl, resume)

    /**
     * Decides on how to continue after a gateway close.
     *
     * @param reason A nullable close reason.
     */
    override suspend fun handleClose(reason: CloseReason?) {
        if (super.state == GatewayState.STOPPED) return // Gateway got stopped on purpose
        if (super.state == GatewayState.RECONNECTING) return  // Gateway is in the process of reconnecting ➜ do not interrupt

        if (reason == null) return reconnect(ReconnectReason.NoCode())

        val discordReason = GatewayClosedReason.fromCode(reason.code) ?: return reconnect(ReconnectReason.UnknownCloseCode())
        if (discordReason.exception) throw Exception(discordReason.cause)

        val reconnectReason = ReconnectReason.Any(discordReason.resume, discordReason.cause)
        return reconnect(reconnectReason)
    }

    /**
     * Reconnects to the gateway.
     *
     * @param reason The reason on why to reconnect.
     */
    suspend fun reconnect(reason: ReconnectReason) {
        super.state = GatewayState.RECONNECTING

        logger.debug("Going to reconnect")
        logger.info("Reconnecting: ${reason.cause}")

        // Close connection if it isn't closed already
        if (connected) {
            socket?.close(CloseReason(4600, "Reconnecting: ${reason.cause}"))
        }

        val finalResumeUrl = resumeUrl
        if (reason.resume && finalResumeUrl != null) connect(finalResumeUrl, true)
        else connect(url, false)
    }

    internal suspend fun sendHeartbeat() {
        // Using the second send function to avoid queueing the call if the socket is null
        send {
            op = OpCode.HEARTBEAT.code
            s = sequence
        }
    }

    /**
     * Updates the applications presence and status.
     * **Is **
     *
     * @param presence New presence / status.
     */
    suspend fun updatePresence(presence: PresenceUpdate) = send(presence)

    internal suspend inline fun <reified T : GatewayCommand> send(command: T) = send {
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
fun Diskord.connectGateway() {
    info(this::class) { "Registering discord event listeners" }
    listenersPackage.forEach { Events.findListeners(it) }

    if (hasWebsocketConnection()) throw Exception("The websocket is already connected")
    val ws = Gateway(this.intents) // Create websocket
    Diskord.apply { gateway = ws }
    ws.connect() // Open websocket connection
}