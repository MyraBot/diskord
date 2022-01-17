package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.ChannelCache
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.rest.request.Promise

interface GetTextChannelBehavior

inline fun <reified T> GetTextChannelBehavior.getChannel(id: String): Promise<T> = ChannelCache[id].map {
    it?.let { channel ->
        when (T::class) {
            ChannelData::class -> channel as T
            DmChannel::class -> DmChannel(channel) as T
            TextChannel::class -> TextChannel(channel) as T
            VoiceChannel::class -> VoiceChannel(channel) as T
            else -> throw IllegalStateException("Unknown channel to cast: ${T::class.simpleName}")
        }
    }
}
