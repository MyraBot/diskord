package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
open class MessageCreateEvent(
    val message: Message
) : Event() {

    override suspend fun prepareEvent() {
        if (message.guildId.missing) PrivateMessageCreateEvent(message)
        else GuildMessageCreateEvent(message)
    }

}