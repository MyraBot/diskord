package diskord.gateway.listeners.impl.guild

import diskord.common.entities.guild.Guild
import diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class GuildCreateEvent(
        val guild: Guild
) : Event()