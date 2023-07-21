package bot.myra.diskord.voice.gateway.events.impl

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@VoiceEvent(Operations.SPEAKING)
class VoiceSpeakingEvent(
    override val gateway: VoiceGateway,
    val data: VoiceSpeakingData
) : GenericVoiceEvent(Operations.SPEAKING, gateway) {
    val userId = data.userId
    val ssrc = data.ssrc
    val speaking = data.speaking

    companion object {
        suspend fun deserialize(json: Json, gateway: VoiceGateway, payload: JsonElement): VoiceSpeakingEvent {
            val data = json.decodeFromJsonElement<VoiceSpeakingData>(payload)
            return VoiceSpeakingEvent(gateway, data).also { it.initialize() }
        }
    }

    override suspend fun initialize() {
        gateway.ssrcToUserId[ssrc] = userId
    }

}

@Serializable
data class VoiceSpeakingData(
    @SerialName("user_id") val userId: String,
    val speaking: Int,
    val delay: Int = 0, // This fields seems to be missing when it gets received
    val ssrc: UInt,
) : VoiceCommand(Operations.SPEAKING)