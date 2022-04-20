package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.caching.GuildCacheAssociation
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent

class ChannelCachePolicy : GenericCachePolicy<String, ChannelData>() {
    val guildAssociation = GuildCacheAssociation<ChannelData>()

    @ListenTo(ChannelCreateEvent::class)
    fun onChannelCreate(event: ChannelCreateEvent) = updateChannel(event.channelData)

    @ListenTo(ChannelUpdateEvent::class)
    fun onChannelUpdate(event: ChannelUpdateEvent) = updateChannel(event.channelData)

    private fun updateChannel(channel: ChannelData) {
        update(channel)
        guildAssociation.update(channel)
    }

    @ListenTo(ChannelDeleteEvent::class)
    fun onChannelDelete(event: ChannelDeleteEvent) {
        remove(event.channelData.id)
        val guild = event.channelData.guildId.value ?: return
        guildAssociation.remove(guild, event.channelData.id)
    }

}