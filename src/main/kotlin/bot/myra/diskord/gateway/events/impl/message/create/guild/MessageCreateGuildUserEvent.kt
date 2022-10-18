package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message

@Suppress("unused")
class MessageCreateGuildUserEvent(
    override val message: Message
) : GenericGuildMessageCreateEvent(message) {
    override suspend fun getMember(): Member = message.getMember()!!
}