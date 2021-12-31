package diskord.rest.behaviors

import diskord.common.caching.ChannelCache
import diskord.common.entities.Channel
import diskord.common.entities.channel.TextChannel
import diskord.common.entities.channel.VoiceChannel

interface GetTextChannelBehavior {

}

inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): T? {
    val channel = ChannelCache[id] ?: return null
    return when (T::class) {
        Channel::class -> channel
        TextChannel::class -> TextChannel(channel)
        VoiceChannel::class -> VoiceChannel(channel)
        else -> throw IllegalStateException()
    } as T
}