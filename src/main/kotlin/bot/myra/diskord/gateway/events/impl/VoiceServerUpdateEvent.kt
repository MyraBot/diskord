package bot.myra.diskord.gateway.events.impl

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceServerUpdateEvent(
    val token: String,
    @SerialName("guild_id") val guildId: String,
    val endpoint: String
)