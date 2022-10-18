package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.message.AnonymousMessage
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.Serializable

@Serializable
open class MessageDeleteEvent(
    val message: AnonymousMessage
) : Event()