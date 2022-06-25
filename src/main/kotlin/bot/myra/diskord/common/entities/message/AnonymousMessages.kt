package bot.myra.diskord.common.entities.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnonymousMessages(
    val ids: List<String>,
    @SerialName("channel_id") val channelId: String,
    @SerialName("guild_id") val guildId: String?
)