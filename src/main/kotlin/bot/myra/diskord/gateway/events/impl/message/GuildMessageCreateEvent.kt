package bot.myra.diskord.gateway.events.impl.message

import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

@Suppress("unused")
class GuildMessageCreateEvent(
    val message: Message
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = message.getGuildAsync()
    fun getMemberAsync(): Deferred<Member> = message.getMemberAsync()
    fun getChannelAsync(): Deferred<TextChannel?> = message.getChannelAsAsync()

}