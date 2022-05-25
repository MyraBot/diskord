package bot.myra.diskord.voice.gateway.commands

import bot.myra.diskord.voice.gateway.models.Operations
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
abstract class VoiceCommand(
    @Transient val operation: Operations = Operations.INVALID
)