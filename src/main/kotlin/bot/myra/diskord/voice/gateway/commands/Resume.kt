package bot.myra.diskord.voice.gateway.commands

import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Used to resume a voice gateway connection.
 *
 * @property guildId The server ID where the bot is.
 * @property session The voice session ID.
 * @property token The bot token.
 * @constructor Create empty Resume payload
 */
@Serializable
data class Resume(
    @SerialName("server_id") val guildId: String,
    @SerialName("session_id") val session: String,
    val token: String
) : VoiceCommand(Operations.RESUME)