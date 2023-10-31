package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.rest.request.Result

@Suppress("unused")
class MessageCreateGuildUserEvent(
    override val message: Message,
    override val diskord: Diskord
) : GenericGuildMessageCreateEvent(message) {
    val guildId = message.guildId.value!!

    suspend fun getMember(): Member = message.getMember()!!
    suspend fun getGuild(): Result<Guild> = message.getGuild()!!
}