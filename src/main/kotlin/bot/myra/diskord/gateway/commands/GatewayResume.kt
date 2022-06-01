package bot.myra.diskord.gateway.commands

import bot.myra.diskord.gateway.OpCode
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#resume-resume-structure)
 * Gets send when the websocket connection needs to be resumed.
 */
@Serializable
data class GatewayResume(
    val token: String,
    @SerialName("session_id") val sessionId: String,
    val seq: Int
) : GatewayCommand(OpCode.RESUME)