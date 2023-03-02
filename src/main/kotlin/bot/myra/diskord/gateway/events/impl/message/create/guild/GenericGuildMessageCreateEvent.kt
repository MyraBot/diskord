package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.impl.message.create.GenericMessageCreateEvent

abstract class GenericGuildMessageCreateEvent(
    override val message: Message
) : GenericMessageCreateEvent(message) {
    suspend inline fun <reified T> getChannelAs() = message.getChannelAs<T>()
}