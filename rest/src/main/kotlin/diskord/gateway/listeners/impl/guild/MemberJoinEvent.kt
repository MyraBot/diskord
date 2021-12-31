package diskord.gateway.listeners.impl.guild

import diskord.common.entities.guild.Member
import diskord.gateway.listeners.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberJoinEvent(
    val member: Member
) : Event()