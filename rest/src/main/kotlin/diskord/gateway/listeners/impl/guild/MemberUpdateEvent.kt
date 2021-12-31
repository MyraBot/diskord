package diskord.gateway.listeners.impl.guild

import diskord.common.entities.guild.Member
import diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class MemberUpdateEvent(
        val member: Member
) : Event()