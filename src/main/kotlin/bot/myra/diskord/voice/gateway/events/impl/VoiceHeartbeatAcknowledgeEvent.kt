package bot.myra.diskord.voice.gateway.events.impl

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

@VoiceEvent(Operations.HEARTBEAT_ACKNOWLEDGE)
class VoiceHeartbeatAcknowledgeEvent(
    override val gateway: VoiceGateway,
    val previousNonce: Long
) : GenericVoiceEvent(Operations.HEARTBEAT_ACKNOWLEDGE, gateway) {

    companion object {
        suspend fun deserialize(json: Json, gateway: VoiceGateway, payload: JsonElement): VoiceHeartbeatAcknowledgeEvent {
            val previousNonce = payload.jsonPrimitive.long
            return VoiceHeartbeatAcknowledgeEvent(gateway, previousNonce).also { it.initialize() }
        }
    }

    override suspend fun initialize() {
        if (previousNonce != gateway.lastTimestamp) gateway.logger.warn("Received non matching heartbeat")
        else gateway.logger.debug("Acknowledged heartbeat!")
    }

}