package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.ChannelCache
import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.channel.TextChannel

interface GetTextChannelBehavior {

}

inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): T? {
    val channel = ChannelCache[id] ?: return null
    return when (T::class) {
        Channel::class -> channel
        TextChannel::class -> TextChannel(channel)
        else -> throw IllegalStateException()
    } as T
}