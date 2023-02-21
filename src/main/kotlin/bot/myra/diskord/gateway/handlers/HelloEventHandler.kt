package bot.myra.diskord.gateway.handlers

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.OpCode
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.gateway.commands.GatewayResume
import bot.myra.diskord.gateway.commands.IdentifyResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

internal class HelloEventHandler(
    override val gateway: Gateway,
    val diskord: Diskord
) : GatewayEventHandler(OpCode.HELLO, gateway) {
    private var heartbeatJob: Job? = null

    override suspend fun onEvent(packet: OpPacket) {
        startHeartbeat(packet)
        if (gateway.resumedConnection) resume() else identify()
        gateway.logger.info("Successfully connected to Discord")
        gateway.sendQueuedCalls()
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#identifying)
     *
     * Identifies the bot to the websocket server, gets send after receiving the ready event.
     * With this the bot loads its required intents.
     */
    private suspend fun identify() {
        val intents = gateway.diskord.intents
        gateway.logger.info("Logging in with intents of $intents (${GatewayIntent.getID(intents)})")
        gateway.send(IdentifyResponse(diskord.token, GatewayIntent.getID(intents), IdentifyResponse.Properties()))
    }

    /**
     * [Documentation](https://discord.com/developers/docs/topics/gateway#resuming)
     * Resumes an old websocket connection.
     */
    private suspend fun resume() {
        gateway.logger.info("Resuming connection...")
        gateway.send(GatewayResume(diskord.token, gateway.session, gateway.sequence))
    }

    /**
     * Starts the heartbeat loop.
     *
     * @param income The received [OpPacket].
     */
    private fun startHeartbeat(income: OpPacket) {
        heartbeatJob?.cancel()

        val heartbeatInterval = income.d!!.jsonObject["heartbeat_interval"]!!.jsonPrimitive.long
        heartbeatJob = gateway.coroutineScope.launch {
            while (isActive) {
                gateway.sendHeartbeat()
                delay(heartbeatInterval)
            }
        }
    }

}