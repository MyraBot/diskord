package bot.myra.diskord.voice.gateway.commands

import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Identify(
    @SerialName("server_id") val guildId: String,
    @SerialName("user_id") val userId: String,
    @SerialName("session_id") val session: String,
    val token: String
) : VoiceCommand(Operations.IDENTIFY)