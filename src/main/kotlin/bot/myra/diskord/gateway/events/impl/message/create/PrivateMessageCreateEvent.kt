package bot.myra.diskord.gateway.events.impl.message.create

import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.types.Event

class PrivateMessageCreateEvent(
    val message: Message
) : Event() {

    suspend fun getChannel() = message.getChannelAs<DmChannel>()

}