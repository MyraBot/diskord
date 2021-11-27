package com.github.myraBot.diskord.gateway

import com.github.m5rian.discord.*
import com.github.myraBot.diskord.Diskord
import com.github.myraBot.diskord.gateway.listeners.Events
import com.github.myraBot.diskord.utilities.GATEWAY_CLIENT
import com.github.myraBot.diskord.utilities.JSON
import com.github.myraBot.diskord.utilities.logging.debug
import com.github.myraBot.diskord.utilities.logging.info
import com.github.myraBot.diskord.utilities.logging.trace
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 *
 * The Gateway websocket to listen to discord events.
 */
object Websocket {
    private const val url = "wss://gateway.discord.gg/?v=9&encoding=json"
    lateinit var session: String
    private val coroutineScope = CoroutineScope(Dispatchers.Default + CoroutineName("Websocket"))
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
                info(this::class) { "Opened websocket connection" }
                while (true) {
                    val data = incoming.receive() as? Frame.Text
                    trace(this::class) { "Incoming websocket message: ${ data?.readText()}" }
                    val income = JSON.decodeFromString<OptCode>(data!!.readText())
                    handleIncome(this, income, resume)
                }
            })
        } catch (e: ClosedReceiveChannelException) {
            info(this::class) { "Lost connection..." }
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
                debug(this::class) { "Received Heartbeat attack!" }
                sendHeartbeat(websocket)
            }
            // 	Sent immediately after connecting
            10 -> {
                startHeartbeat(websocket, income)
                if (resume) resume(websocket)
                else identify(websocket)
                info(this::class) { "Successfully connected to Discord" }
            }
            // 	Sent in response to receiving a heartbeat to acknowledge that it has been received
            11 -> {
                trace(this::class) { "Heartbeat acknowledged!" }
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
        trace(this::class) { "Sent heartbeat!" }
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
        info(this::class) { "Connecting with intents of ${Diskord.intents} (${GatewayIntent.getID(Diskord.intents)})" }
        val d = IdentifyResponse(Diskord.token, GatewayIntent.getID(Diskord.intents), Properties())
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
        info(this::class) { "Reconnecting to Discord" }
        val json = JSON.encodeToJsonElement(GatewayResume(
            token = Diskord.token,
            sessionId = session,
            seq = s
        )).jsonObject
        websocket.send(OptCode(null, null, 6, json).toJson())
    }

}