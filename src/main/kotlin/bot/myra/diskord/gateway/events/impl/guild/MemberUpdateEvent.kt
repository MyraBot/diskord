package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.gateway.events.Event
import kotlinx.coroutines.Deferred
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateEvent(
    val member: Member,
) : Event() {

    fun getGuildAsync(): Deferred<Guild?> = member.getGuildAsync()

}
