package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.entities.message.Message

@Suppress("unused")
class MessageCreateGuildWebhookEvent(
    override val message: Message
) : GenericGuildMessageCreateEvent(message)