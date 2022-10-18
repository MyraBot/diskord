package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateEvent(
    val member: Member,
) : Event() {

    suspend fun getGuild():Guild? = member.getGuild()

}
