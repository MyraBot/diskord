package diskord.gateway.listeners.impl.guild

import diskord.common.entities.User
import diskord.gateway.listeners.Event

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-member-add)
 */
data class MemberRemoveEvent(
    val user: User,
    val guildId: String
) : Event()