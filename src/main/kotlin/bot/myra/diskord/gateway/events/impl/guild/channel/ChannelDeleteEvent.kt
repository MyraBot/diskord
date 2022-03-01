package bot.myra.diskord.gateway.events.impl.guild.channel

import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
data class ChannelDeleteEvent(
        val channelData: ChannelData
) : Event()