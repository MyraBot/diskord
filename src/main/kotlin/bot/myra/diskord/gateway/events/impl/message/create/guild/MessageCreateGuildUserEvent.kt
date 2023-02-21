package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message

@Suppress("unused")
class MessageCreateGuildUserEvent(
    override val message: Message,
    override val diskord: Diskord
) : GenericGuildMessageCreateEvent(message) {
    suspend fun getMember(): Member = message.getMember()!!
}