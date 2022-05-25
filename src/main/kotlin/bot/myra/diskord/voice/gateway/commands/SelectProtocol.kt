package bot.myra.diskord.voice.gateway.commands

import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.Serializable

@Serializable
data class SelectProtocol(
    val protocol: String,
    val data: ProtocolDetails
) : VoiceCommand(Operations.SELECT_PROTOCOL)

@Serializable
data class ProtocolDetails(
    val address: String,
    val port: Int,
    val mode: String
)