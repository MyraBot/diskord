package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.message.Message

class MessageCreateGuildWebhookEvent(
    override val message: Message,
    override val diskord: Diskord
) : GenericGuildMessageCreateEvent(message)