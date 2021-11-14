package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import com.github.myraBot.diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking


object ChannelCache : Cache<String, Channel>() {

    inline fun <reified T> getAs(id: String): T? {
        val channel = ChannelCache[id] ?: return null
        return when (T::class) {
            Channel::class -> channel
            TextChannel::class -> TextChannel(channel)
            else -> throw IllegalStateException()
        } as T
    }

    override fun retrieve(key: String): Channel? {
        println("Retrieving following channel: $key")
        return runBlocking {
            Endpoints.getChannel.execute { arg("channel.id", key) }
        }
    }

    @ListenTo(ChannelCreateEvent::class)
    fun onChannelCreate(event: ChannelCreateEvent) = update(event.channel)

    @ListenTo(ChannelUpdateEvent::class)
    fun onChannelUpdate(event: ChannelUpdateEvent) = update(event.channel)

    @ListenTo(ChannelDeleteEvent::class)
    fun onChannelDelete(event: ChannelDeleteEvent) = update(event.channel)

    override fun update(value: Channel) {
        println("Updating channel $value")
        map[value.id] = value
    }

}