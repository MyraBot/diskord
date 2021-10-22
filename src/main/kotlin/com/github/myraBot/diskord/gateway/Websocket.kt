package com.github.myraBot.diskord.gateway

import com.github.m5rian.discord.*
import com.github.myraBot.diskord.gateway.listeners.Events
import com.github.myraBot.diskord.rest.Endpoints
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import java.util.concurrent.Executors
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.TimeUnit

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
 */
object Websocket {
    private val coroutineScope = CoroutineScope(ForkJoinPool().asCoroutineDispatcher())
    private var s: Int = 0

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#gateways)
     *
     * Calls [openWebsocketConnection] to connect to the websocket first time.
     */
    suspend fun connect() {
        val response = CLIENT.get<HttpResponse>("${Endpoints.baseUrl}/gateway/bot") {
            header("Authorization", "Bot ${DiscordBot.token}")
        }.readText()
        val json = JSON.decodeFromString<JsonObject>(response)

        val url = json["url"]!!.jsonPrimitive.content + "?v=9&encoding=json"
        openWebsocketConnection(url)
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/opcodes-and-status-codes#gateway)
     *
     * Runs the websocket loop.
     */
    private suspend fun openWebsocketConnection(url: String) {
        CLIENT.webSocket(url, {}, {
            info(this::class) { "Opened websocket connection" }
            while (true) {
                val data = incoming.receive() as? Frame.Text
                trace(this::class) { data?.readText() }
                val income = JSON.decodeFromString<OptCode>(data!!.readText())
                when (income.op) {
                    // 	An event was dispatched
                    0 -> {
                        Events.resolve(income)
                        s = income.s ?: s
                    }
                    // 	Fired periodically by the client to keep the connection alive
                    1 -> {
                        debug(this::class) { "Received Heartbeat attack!" }
                        sendHeartbeat(this)
                    }
                    // 	Sent immediately after connecting
                    10 -> {
                        startHeartbeat(this, income)
                        identify(this)
                        info(this::class) { "Successfully connected to Discord" }
                    }
                    // 	Sent in response to receiving a heartbeat to acknowledge that it has been received
                    11 -> {
                        trace(this::class) { "Heartbeat acknowledged!" }
                    }
                }
            }
        })
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
     * Identifies the bot to the websocket server.
     * With this the bot loads its required intents.
     *
     * @param websocket
     */
    private suspend fun identify(websocket: DefaultClientWebSocketSession) {
        val d = IdentifyResponse(DiscordBot.token, 512, Properties())
        val jsonObject = Json.encodeToJsonElement(d).jsonObject
        websocket.send(OptCode(null, null, 2, jsonObject).toJson())
    }

}