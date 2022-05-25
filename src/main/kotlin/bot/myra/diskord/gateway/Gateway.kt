package bot.myra.diskord.gateway

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.GenericGateway
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.events.Events
import bot.myra.kommons.debug
import bot.myra.kommons.info
import bot.myra.kommons.kInfo
import bot.myra.kommons.trace
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
class Gateway(
    private val intents: MutableSet<GatewayIntent> = mutableSetOf(),
) : GenericGateway(
    url = "wss://gateway.discord.gg/?v=9&encoding=json",
    logger = LoggerFactory.getLogger(Gateway::class.java)
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Websocket"))
    lateinit var session: String
    private var s: Int = 0
    val eventDispatcher = MutableSharedFlow<Opcode>()

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Starts the event resolver and opens a gateway websocket connection.
     */
    suspend fun connect() {
        Events.startResolver()
        openGatewayConnection()
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#identifying)
     *
     * Identifies the bot to the websocket server, gets send after receiving the ready event.
     * With this the bot loads its required intents.
     */
    private suspend fun identify() {
        kInfo(this::class) { "Logging in with intents of $intents (${GatewayIntent.getID(intents)})" }
        val op = IdentifyResponse(
            token = Diskord.token,
            intents = GatewayIntent.getID(intents),
            properties = Properties()
        )
        send(Opcode(null, null, 2, op.toJsonObj()))
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#resuming)
     * Resumes an old websocket connection.
     */
    private suspend fun resume() {
        kInfo(this::class) { "Reconnecting to Discord" }
        val op = GatewayResume(
            token = Diskord.token,
            sessionId = session,
            seq = s
        )
        send(Opcode(null, null, 6, op.toJsonObj()))
    }

    override suspend fun handleIncome(opcode: Opcode, resumed: Boolean) {
        when (opcode.op) {
            // 	An event was dispatched
            0  -> {
                eventDispatcher.emit(opcode)
                s = opcode.s ?: s
            }
            // 	Fired periodically by the client to keep the connection alive
            1  -> {
                debug(this::class) { "Received Heartbeat attack!" }
                sendHeartbeat()
            }
            // 	Sent immediately after connecting
            10 -> {
                startHeartbeat(opcode)
                if (resumed) resume()
                else identify()
                ready()
                info(this::class) { "Successfully connected to Discord" }
            }
            // 	Sent in response to receiving a heartbeat to acknowledge that it has been received
            11 -> {
                debug(this::class) { "Heartbeat acknowledged!" }
            }
        }
    }

    /**
     * Starts the heartbeat loop.
     *
     * @param income The received [Opcode].
     */
    private fun startHeartbeat(income: Opcode) {
        val heartbeatInterval = income.d!!.jsonObject["heartbeat_interval"]!!.jsonPrimitive.int
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            coroutineScope.launch {
                sendHeartbeat()
            }
        }, 0, heartbeatInterval.toLong() - 2500, TimeUnit.MILLISECONDS)
    }

    /**
     * Sends a heartbeat response to Discord.
     */
    private suspend fun sendHeartbeat() {
        val heartbeat = Opcode(null, null, 1, s)
        send(heartbeat)
        trace(this::class) { "Sent heartbeat!" }
    }

    /**
     * Updates the applications presence and status.
     * **Is **
     *
     * @param presence New presence / status.
     */
    suspend fun updatePresence(presence: PresenceUpdate) {
        send(Opcode(null, null, 3, presence.toJsonObj(true)))
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