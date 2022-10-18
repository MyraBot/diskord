package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.types.EventBroker


class GuildMessageCreateEventBroker(
    val message: Message
) : EventBroker() {

    override suspend fun choose() = when (message.isWebhook) {
        true  -> MessageCreateGuildWebhookEvent(message)
        false -> MessageCreateGuildUserEvent(message)
    }

}