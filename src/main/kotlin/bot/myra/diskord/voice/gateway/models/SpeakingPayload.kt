package bot.myra.diskord.voice.gateway.models

import bot.myra.diskord.voice.gateway.commands.VoiceCommand
import kotlinx.serialization.Serializable

@Serializable
data class SpeakingPayload(
    val speaking: Int,
    val delay: Int,
    val ssrc: Int
) : VoiceCommand(Operations.SPEAKING)