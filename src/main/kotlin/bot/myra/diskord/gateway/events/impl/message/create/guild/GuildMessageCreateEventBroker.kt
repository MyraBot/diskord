package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.types.EventBroker


class GuildMessageCreateEventBroker(
    val message: Message,
    override val diskord: Diskord
) : EventBroker() {

    override suspend fun choose() = when (message.isWebhook) {
        true  -> MessageCreateGuildWebhookEvent(message, diskord)
        false -> MessageCreateGuildUserEvent(message, diskord)
    }

}