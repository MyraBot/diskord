package com.github.myraBot.diskord.rest.behaviors

import com.github.myraBot.diskord.common.caching.ChannelCache
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.channel.DmChannel
import com.github.myraBot.diskord.common.entities.channel.TextChannel
import com.github.myraBot.diskord.common.entities.channel.VoiceChannel
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

interface GetTextChannelBehavior

inline fun <reified T> GetTextChannelBehavior.getChannelAsync(id: String): Deferred<T?> {
    val future = CompletableDeferred<T?>()
    RestClient.coroutineScope.launch {
        val data = ChannelCache.get(id).await()
        if (data == null){
            future.complete(null)
            return@launch
        }
        val channel = when (T::class) {
            ChannelData::class -> data
            DmChannel::class -> DmChannel(data)
            TextChannel::class -> TextChannel(data)
            VoiceChannel::class -> VoiceChannel(data)
            else -> throw IllegalStateException("Unknown channel to cast: ${T::class.simpleName}")
        } as T
        future.complete(channel)
    }
    return future
}
