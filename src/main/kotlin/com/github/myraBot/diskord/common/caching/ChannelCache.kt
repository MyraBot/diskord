package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking


object ChannelCache : Cache<String, ChannelData>() {

    inline fun <reified T> getAs(id: String): T? {
        val channel = ChannelCache[id] ?: return null
        return when (T::class) {
            DmChannel::class -> DmChannel(channel) as T
            TextChannel::class -> TextChannel(channel) as T
            VoiceChannel::class -> VoiceChannel(channel) as T
            else -> throw IllegalStateException("Unknown channel to cast: ${T::class.simpleName}")
        }
    }

    override fun retrieve(key: String): ChannelData? {
        return runBlocking {
            Endpoints.getChannel.execute { arg("channel.id", key) }
        }
    }

    @ListenTo(ChannelCreateEvent::class)
    fun onChannelCreate(event: ChannelCreateEvent) = update(event.channelData)

    @ListenTo(ChannelUpdateEvent::class)
    fun onChannelUpdate(event: ChannelUpdateEvent) = update(event.channelData)

    @ListenTo(ChannelDeleteEvent::class)
    fun onChannelDelete(event: ChannelDeleteEvent) = update(event.channelData)

    override fun update(value: ChannelData) {
        map[value.id] = value
    }

}