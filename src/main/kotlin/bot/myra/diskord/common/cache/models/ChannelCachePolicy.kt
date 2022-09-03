package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericCachePolicy
import bot.myra.diskord.common.cache.GuildCacheAssociation
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GuildLeaveEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent

class MutableChannelCachePolicy : ChannelCachePolicy() {

    @ListenTo(ChannelCreateEvent::class)
    suspend fun onChannelCreate(event: ChannelCreateEvent) = updateChannel(event.channelData)

    @ListenTo(ChannelUpdateEvent::class)
    suspend fun onChannelUpdate(event: ChannelUpdateEvent) = updateChannel(event.channelData)

    @ListenTo(ChannelDeleteEvent::class)
    suspend fun onChannelDelete(event: ChannelDeleteEvent) {
        remove(event.channelData.id)
        val guild = event.channelData.guildId.value ?: return
        guildAssociation.remove(guild, event.channelData.id)
    }

    @ListenTo(GuildLeaveEvent::class)
    suspend fun onGuildLeave(event: GuildLeaveEvent) = view().filter { it.guildId.value == event.guild.id }.forEach { remove(it.id) }

}

class DisabledChannelCachePolicy : ChannelCachePolicy()

abstract class ChannelCachePolicy : GenericCachePolicy<String, ChannelData>() {
    val guildAssociation = GuildCacheAssociation<ChannelData>()

    suspend fun updateChannel(channel: ChannelData) {
        update(channel)
        guildAssociation.update(channel)
    }

}