package bot.myra.diskord.gateway.events.impl.message.create

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.impl.message.create.guild.GuildMessageCreateEventBroker
import bot.myra.diskord.gateway.events.types.EventBroker

class MessageCreateEventBroker(
    val message: Message
) : EventBroker() {

    override suspend fun choose() = when (message.guildId.missing) {
        true  -> PrivateMessageCreateEvent(message)
        false -> GuildMessageCreateEventBroker(message)
    }

}