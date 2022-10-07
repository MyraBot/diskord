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

    override fun handleClose(reason: CloseReason?) {
        if (reason == null) return
        val discordReason = GatewayClosedReason.fromCode(reason.code) ?: return
        if (discordReason.exception) throw Exception(discordReason.cause)
        else {
            if (discordReason.resume) connect(resumeUrl ?: url, true)
            else connect(url)
        }
    }

    internal suspend fun sendHeartbeat() {
        // Using the second send function to avoid queueing the call if the socket is null
        send {
            op = OpCode.HEARTBEAT.code
            s = sequence
        }
    }

    suspend fun reconnect(reason: GatewayReconnectReason) {
        socket?.close(CloseReason(4600, "Reconnecting: ${reason.cause}"))

        if (reason.resume) resumeUrl?.let {
            openSocketConnection(it, true)
            return
        }
        // Either should not resume or resumeUrl is null
        openSocketConnection(url, false)
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