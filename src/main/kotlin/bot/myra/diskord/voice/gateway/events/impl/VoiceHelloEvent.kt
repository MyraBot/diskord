package bot.myra.diskord.voice.gateway.events.impl

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.commands.Identify
import bot.myra.diskord.voice.gateway.commands.Resume
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement

/**
 * Runs when the client received the acknowledgment from discord ([Operations.HELLO]).
 * Does identification and starts the heartbeat.
 *
 * @param gateway The used [VoiceGateway].
 */
@VoiceEvent(Operations.HELLO)
class VoiceHelloEvent(
    override val gateway: VoiceGateway,
    val data: VoiceHelloData
) : GenericVoiceEvent(Operations.HELLO, gateway) {

    companion object {
        suspend fun deserialize(json: Json, gateway: VoiceGateway, payload: JsonElement): GenericVoiceEvent {
            val data = json.decodeFromJsonElement<VoiceHelloData>(payload)
            return VoiceHelloEvent(gateway, data).also { it.initialize() }
        }
    }

    override suspend fun initialize() {
        gateway.apply {
            if (resumedConnection) {
                logger.info("Resuming connection $session")
                send(Resume(guildId, session, token))
            } else {
                logger.info("Creating connection $session")
                send(Identify(guildId, diskord.id, session, token))
            }
        }

        startHeartbeat()
    }

    private fun startHeartbeat() {
        gateway.scope.launch {
            val interval = data.heartbeatInterval.toLong()

            gateway.awaitEvent<VoiceReadyEvent>()
            val timestamp: Long = System.currentTimeMillis()

            while (true) {
                gateway.apply {
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

}

@Serializable
data class VoiceHelloData(
    val v: Int,
    @SerialName("heartbeat_interval") val heartbeatInterval: Double
)