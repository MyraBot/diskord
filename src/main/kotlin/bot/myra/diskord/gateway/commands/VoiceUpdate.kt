package bot.myra.diskord.gateway.commands

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VoiceUpdate(
    @SerialName("guild_id") val guildId: String,
    @SerialName("channel_id") val channelId: String?,
    @SerialName("self_mute") val selfMute: Boolean,
    @SerialName("self_deaf") val selfDeaf: Boolean
)