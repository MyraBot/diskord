package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.JSON
import com.github.myraBot.diskord.common.utilities.GATEWAY_CLIENT
import com.github.myraBot.diskord.common.utilities.kDebug
import com.github.myraBot.diskord.common.utilities.kInfo
import com.github.myraBot.diskord.common.utilities.kTrace
import com.github.myraBot.diskord.gateway.listeners.Events
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
class Websocket(
    val intents: MutableSet<GatewayIntent> = mutableSetOf()
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Websocket"))
    private val url = "wss://gateway.discord.gg/?v=9&encoding=json"
    lateinit var session: String
    private var s: Int = 0

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Calls [openWebsocketConnection] to connect to the websocket first time.
     */
    suspend fun connect() {
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
            GATEWAY_CLIENT.webSocket(url, {}, {
                kInfo(this::class) { "Opened websocket connection" }
                while (true) {
                    val data = incoming.receive() as? Frame.Text
                    kDebug(this::class) { "Gateway << ${data?.readText()}" }
                    val income = JSON.decodeFromString<OptCode>(data!!.readText())
                    handleIncome(this, income, resume)
                }
            })
        } catch (e: ClosedReceiveChannelException) {
            kInfo(this::class) { "Lost connection..." }
            openWebsocketConnection(url, true)
        }
    }

    private suspend fun handleIncome(websocket: DefaultClientWebSocketSession, income: OptCode, resume: Boolean) {
        when (income.op) {
            // 	An event was dispatched
            0 -> {
                Events.resolve(income)
                s = income.s ?: s
            }
            // 	Fired periodically by the client to keep the connection alive
            1 -> {
                kDebug(this::class) { "Received Heartbeat attack!" }
                sendHeartbeat(websocket)
            }
            // 	Sent immediately after connecting
            10 -> {
                startHeartbeat(websocket, income)
                if (resume) resume(websocket)
                else identify(websocket)
                kInfo(this::class) { "Successfully connected to Discord" }
            }
            // 	Sent in response to receiving a heartbeat to acknowledge that it has been received
            11 -> {
                kDebug(this::class) { "Heartbeat acknowledged!" }
            }
        }
    }

    /**
     * Starts the heartbeat loop.
     *
     * @param websocket The opened websocket connection.
     * @param income The received [OptCode].
     */
    private fun startHeartbeat(websocket: DefaultClientWebSocketSession, income: OptCode) {
        val heartbeatInterval = income.d!!.jsonObject["heartbeat_interval"]!!.jsonPrimitive.int
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate({
            coroutineScope.launch {
                sendHeartbeat(websocket)
            }
        }, 0, heartbeatInterval.toLong() - 2500, TimeUnit.MILLISECONDS)
    }

    /**
     * Sends a heartbeat response to Discord.
     *
     * @param websocket The opened websocket connection.
     */
    private suspend fun sendHeartbeat(websocket: DefaultClientWebSocketSession) {
        val heartbeat = OptCode(null, null, 1, s).toJson()
        websocket.send(heartbeat)
        kTrace(this::class) { "Sent heartbeat!" }
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#identifying)
     *
     * Identifies the bot to the websocket server, gets send after receiving the ready event.
     * With this the bot loads its required intents.
     *
     * @param websocket
     */
    private suspend fun identify(websocket: DefaultClientWebSocketSession) {
        kInfo(this::class) { "Logging in with intents of $intents (${GatewayIntent.getID(intents)})" }
        val d = IdentifyResponse(Diskord.token, GatewayIntent.getID(intents), Properties())
        val jsonObject = Json.encodeToJsonElement(d).jsonObject
        websocket.send(OptCode(null, null, 2, jsonObject).toJson())
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#resuming)
     * Resumes an old websocket connection.
     *
     * @param websocket The web
     */
    private suspend fun resume(websocket: DefaultClientWebSocketSession) {
        kInfo(this::class) { "Reconnecting to Discord" }
        val json = JSON.encodeToJsonElement(GatewayResume(
            token = Diskord.token,
            sessionId = session,
            seq = s
        )).jsonObject
        websocket.send(OptCode(null, null, 6, json).toJson())
    }

}

/**
 * Registers all registered events from [listeners],
 * initialises [Diskord] and starts the websocket.
 *
 * @return Returns the [Diskord] object. Just for laziness.
 */
suspend fun Diskord.connectGateway() {
    if (hasWebsocketConnection()) throw Exception("The websocket is already connected")
    val ws = Websocket(this.intents) // Create websocket
    Diskord.apply { websocket = ws }
    ws.connect() // Open websocket connection
}