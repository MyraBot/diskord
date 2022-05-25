package bot.myra.diskord.gateway.handler

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.common.utilities.toJsonObj
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.events.Events
import bot.myra.diskord.gateway.handler.intents.GatewayIntent
import bot.myra.kommons.*
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
class Websocket(
    private val intents: MutableSet<GatewayIntent> = mutableSetOf()
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Websocket"))
    private val url = "wss://gateway.discord.gg/?v=9&encoding=json"

    lateinit var session: String
    private var s: Int = 0
    private var connection: DefaultClientWebSocketSession? = null
    private val waitingCalls: MutableList<OptCode> = mutableListOf()
    val connected get() = connection != null

    val eventDispatcher = MutableSharedFlow<OptCode>()

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Calls [openWebsocketConnection] to connect to the websocket first time.
     */
    suspend fun connect() {
        Events.startResolver()
        openWebsocketConnection(url, false)
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway)
     * Runs the websocket loop.
     *
     * @param url The websocket url to connect to.
     * @param resume Whether an already existing connection should resume or not
     */
    private suspend fun openWebsocketConnection(url: String, resume: Boolean) {
        try {
            Diskord.gatewayClient.webSocket(url, {}, {
                kInfo(this::class) { "Opened websocket connection" }
                connection = this
                waitingCalls.forEach { send(it.toJson()) }

                incoming.receiveAsFlow().collect {
                    val data = it as Frame.Text
                    kDebug(this::class) { "Gateway << ${data.readText()}" }
                    val income = JSON.decodeFromString<OptCode>(data.readText())
                    handleIncome(income, resume)
                }
            })
        } catch (e: ClosedReceiveChannelException) {
            info(this::class) { "Lost connection..." }
            connection = null

            openWebsocketConnection(url, true)
        }
    }

    private suspend fun handleIncome(income: OptCode, resume: Boolean) {
        when (income.op) {
            // 	An event was dispatched
            0  -> {
                eventDispatcher.emit(income)
                s = income.s ?: s
            }
            // 	Fired periodically by the client to keep the connection alive
            1  -> {
                debug(this::class) { "Received Heartbeat attack!" }
                sendHeartbeat()
            }
            // 	Sent immediately after connecting
            10 -> {
                startHeartbeat(income)
                if (resume) resume()
                else identify()
                info(this::class) { "Successfully connected to Discord" }
            }
            // 	Sent in response to receiving a heartbeat to acknowledge that it has been received
            11 -> {
                debug(this::class) { "Heartbeat acknowledged!" }
            }
        }
    }

    /**
     * Sends the provided opt code to the websocket.
     * If the websocket isn't connected, the opt-code will get added to [waitingCalls].
     * All waiting calls get executed as soon as the websocket is connected again.
     * As a result, **this is function is **
     *
     * @param optCode Opt-code to send.
     */
    internal suspend fun send(optCode: OptCode) {
        debug(this::class) { "Gateway >> ${optCode.toJson()}" }
        connection?.send(optCode.toJson()) ?: waitingCalls.add(optCode)
    }

    /**
     * Starts the heartbeat loop.
     *
     * @param income The received [OptCode].
     */
    private fun startHeartbeat(income: OptCode) {
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
        val heartbeat = OptCode(null, null, 1, s)
        send(heartbeat)
        trace(this::class) { "Sent heartbeat!" }
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
        send(OptCode(null, null, 2, op.toJsonObj()))
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
        send(OptCode(null, null, 6, op.toJsonObj()))
    }

    /**
     * Updates the applications presence and status.
     * **Is **
     *
     * @param presence New presence / status.
     */
    suspend fun updatePresence(presence: PresenceUpdate) {
        send(OptCode(null, null, 3, presence.toJsonObj(true)))
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
    val ws = Websocket(this.intents) // Create websocket
    Diskord.apply { websocket = ws }
    ws.connect() // Open websocket connection
}