package bot.myra.diskord.voice.gateway.events.impl

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.events.GenericVoiceEvent
import bot.myra.diskord.voice.gateway.events.VoiceEvent
import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.decodeFromJsonElement

@VoiceEvent(Operations.SESSION_DESCRIPTION)
class VoiceSessionDescriptionEvent(
    gateway: VoiceGateway,
    val data: SessionDescriptionData
) : GenericVoiceEvent(Operations.SESSION_DESCRIPTION, gateway) {
    val audioCodec = data.audioCodec
    val videoCodec = data.videoCodec
    val secretKey = data.secretKey
    val mode = data.mode
    val mediaSession = data.mediaSession

    companion object {
        fun deserialize(json: Json, gateway: VoiceGateway, payload: JsonElement): VoiceSessionDescriptionEvent {
            val data = json.decodeFromJsonElement<SessionDescriptionData>(payload)
            return VoiceSessionDescriptionEvent(gateway, data)
        }
    }

}

@Serializable
data class SessionDescriptionData(
    @SerialName("audio_codec") val audioCodec: String,
    @SerialName("video_codec") val videoCodec: String?,
    @SerialName("secret_key") val secretKey: List<UByte>,
    val mode: String,
    @SerialName("media_session_id") val mediaSession: String
)