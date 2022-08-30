package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event

@Suppress("unused")
class GuildMessageCreateEvent(
    val message: Message
) : Event() {
    val member: Member = message.member!!

    suspend fun getGuild(): Guild = message.getGuild()!!
    suspend fun getChannel(): TextChannel = message.getChannelAs()!!

}