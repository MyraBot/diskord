package bot.myra.diskord.gateway.events.impl.message.create.guild

import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.impl.message.create.GenericMessageCreateEvent


abstract class GenericGuildMessageCreateEvent(
    override val message: Message
) : GenericMessageCreateEvent(message) {
    suspend fun getGuild() = message.getGuild()!!
    open suspend fun getMember(): Member? = message.getMember()
    suspend fun getChannel() = message.getChannelAs<TextChannel>().value!!
}