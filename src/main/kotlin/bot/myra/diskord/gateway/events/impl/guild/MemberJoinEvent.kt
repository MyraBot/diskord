package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.types.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event() {

    suspend fun getGuild(): Guild = Diskord.getGuild(member.guildId)!!

}