package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event

class PrivateMessageCreateEvent(
    val message: Message
) : Event() {

    suspend fun getChannel():DmChannel? = message.getChannelAs()

}