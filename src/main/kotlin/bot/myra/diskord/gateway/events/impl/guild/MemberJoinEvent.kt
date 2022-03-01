package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = Diskord.getGuildAsync(member.guildId)

}