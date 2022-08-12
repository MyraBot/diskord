package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
open class MessageUpdateEvent(
    val message: Message
) : Event()