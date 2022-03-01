package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

class PrivateMessageCreateEvent(
    val message: Message
) : Event() {

    fun getChannelAsync(): Deferred<DmChannel?> = message.getChannelAsAsync()

}