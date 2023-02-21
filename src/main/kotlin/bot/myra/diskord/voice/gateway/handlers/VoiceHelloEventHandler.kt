package bot.myra.diskord.voice.gateway.handlers

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.utilities.JSON
import bot.myra.diskord.gateway.OpPacket
import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.commands.Identify
import bot.myra.diskord.voice.gateway.commands.Resume
import bot.myra.diskord.voice.gateway.models.HelloPayload
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Runs when the client received the acknowledgment from discord ([Operations.HELLO]).
 * Does identification and starts the heartbeat.
 *
 * @param gateway The used [VoiceGateway].
 * @param diskord The current [Diskord] instance.
 */
internal class VoiceHelloEventHandler(
    gateway: VoiceGateway,
    val diskord: Diskord
) : VoiceGatewayEventHandler(Operations.HELLO, gateway) {

    override suspend fun onEvent(packet: OpPacket) {
        gateway.apply {
            if (resumedConnection) {
                logger.info("Resuming connection $session")
                send(Resume(guildId, session, token))
            } else {
                logger.info("Creating connection $session")
                send(Identify(guildId, diskord.id, session, token))
            }
        }

        startHeartbeat(packet)
    }

    private fun startHeartbeat(hello: OpPacket) = gateway.apply {
        scope.launch {
            val interval = hello.d?.let { JSON.decodeFromJsonElement<HelloPayload>(it) }?.heartbeatInterval?.toLong() ?: throw IllegalStateException("Invalid hello payload")

            eventDispatcher.first { it.op == Operations.READY.code }
            val timestamp: Long = System.currentTimeMillis()

            while (true) {
                lastTimestamp = System.currentTimeMillis() - timestamp
                send {
                    op = Operations.HEARTBEAT.code
                    d = JsonPrimitive(lastTimestamp)
                }
                delay(interval)
            }
        }
    }

}