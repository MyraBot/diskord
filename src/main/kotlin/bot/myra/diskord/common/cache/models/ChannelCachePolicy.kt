package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericGuildCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.GuildLeaveEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent

class MutableChannelCachePolicy : ChannelCachePolicy() {

    override fun isFromGuild(value: ChannelData): String? = value.guildId.value

    @ListenTo(ChannelCreateEvent::class)
    suspend fun onChannelCreate(event: ChannelCreateEvent) = update(event.channelData)

    @ListenTo(ChannelUpdateEvent::class)
    suspend fun onChannelUpdate(event: ChannelUpdateEvent) = update(event.channelData)

    @ListenTo(ChannelDeleteEvent::class)
    suspend fun onChannelDelete(event: ChannelDeleteEvent) {
        remove(event.channelData.id)
    }

    @ListenTo(GuildLeaveEvent::class)
    suspend fun onGuildLeave(event: GuildLeaveEvent) = view().filter { it.guildId.value == event.guild.id }.forEach { remove(it.id) }

}

class DisabledChannelCachePolicy : ChannelCachePolicy() {
    override fun isFromGuild(value: ChannelData): String? = null
}

abstract class ChannelCachePolicy : GenericGuildCachePolicy<String, ChannelData>() {
    override fun getAsKey(value: ChannelData): String = value.id
}