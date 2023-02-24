package bot.myra.diskord.gateway.events.impl.message.create

import bot.myra.diskord.common.entities.channel.text.GenericTextChannel
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.types.Event

abstract class GenericMessageCreateEvent(
    open val message: Message
) : Event() {
    suspend fun getChannel() = message.getChannelAs<GenericTextChannel>()
}