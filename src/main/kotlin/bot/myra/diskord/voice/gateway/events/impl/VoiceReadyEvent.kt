package bot.myra.diskord.voice.gateway.events.impl

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@VoiceEvent(Operations.READY)
data class VoiceReadyEvent(
    override val gateway: VoiceGateway,
    private val data: VoiceReadyData
) : GenericVoiceEvent(Operations.READY, gateway) {
    val ssrc = data.ssrc
    val ip = data.ip
    val port = data.port
    val modes = data.modes

    companion object {
        suspend fun deserialize(json: Json, gateway: VoiceGateway, payload: JsonElement): VoiceReadyEvent {
            val data = json.decodeFromJsonElement<VoiceReadyData>(payload)
            return VoiceReadyEvent(gateway, data).also { it.initialize() }
        }
    }

}

@Serializable
data class VoiceReadyData(
    val ssrc: UInt,
    val ip: String,
    val port: Int,
    val modes: List<String>
)