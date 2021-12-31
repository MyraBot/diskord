package diskord.gateway.listeners.impl.guild.channel

import diskord.common.entities.Channel
import diskord.gateway.listeners.Event
import kotlinx.serialization.Serializable

@Serializable
data class ChannelDeleteEvent(
        val channel: Channel
) : Event()