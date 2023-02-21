package bot.myra.diskord.common.cache.models

import bot.myra.diskord.common.cache.GenericGuildCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.gateway.events.ListenTo
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelCreateEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelDeleteEvent
import bot.myra.diskord.gateway.events.impl.guild.channel.ChannelUpdateEvent
import bot.myra.diskord.gateway.events.impl.guild.delete.GuildLeaveEvent

class MutableChannelCachePolicy : ChannelCachePolicy() {

    override fun isFromGuild(value: ChannelData): String? = value.guildId.value

    @ListenTo(ChannelCreateEvent::class)
    suspend fun onChannelCreate(event: ChannelCreateEvent) = update(event.channel.data)

    @ListenTo(ChannelUpdateEvent::class)
    suspend fun onChannelUpdate(event: ChannelUpdateEvent) = update(event.channel.data)

    @ListenTo(ChannelDeleteEvent::class)
    suspend fun onChannelDelete(event: ChannelDeleteEvent) {
        remove(event.channel.id)
    }

    @ListenTo(GuildLeaveEvent::class)
    suspend fun onGuildLeave(event: GuildLeaveEvent) = remove(event.guild.id)

}

class DisabledChannelCachePolicy : ChannelCachePolicy() {
    override fun isFromGuild(value: ChannelData): String? = null
}

abstract class ChannelCachePolicy : GenericGuildCachePolicy<String, ChannelData>() {
    override fun getAsKey(value: ChannelData): String = value.id
}