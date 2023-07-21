package bot.myra.diskord.voice.gateway.events

import bot.myra.diskord.voice.gateway.VoiceGateway
import bot.myra.diskord.voice.gateway.models.Operations

abstract class GenericVoiceEvent(
    val operation: Operations,
    open val gateway: VoiceGateway
) {
    open suspend fun initialize() = Unit
}