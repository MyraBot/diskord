package diskord.common.caching

import diskord.common.entities.Channel
import diskord.common.entities.channel.TextChannel
import diskord.gateway.listeners.ListenTo
import diskord.gateway.listeners.impl.guild.channel.ChannelCreateEvent
import diskord.gateway.listeners.impl.guild.channel.ChannelDeleteEvent
import diskord.gateway.listeners.impl.guild.channel.ChannelUpdateEvent
import diskord.rest.Endpoints
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
        map[value.id] = value
    }

}