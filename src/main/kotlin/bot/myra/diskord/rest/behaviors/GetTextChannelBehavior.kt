package bot.myra.diskord.rest.behaviors

import bot.myra.diskord.common.caching.ChannelCache
import bot.myra.diskord.common.entities.channel.ChannelData
import bot.myra.diskord.common.entities.channel.DmChannel
import bot.myra.diskord.common.entities.channel.TextChannel
import bot.myra.diskord.common.entities.channel.VoiceChannel
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch

interface GetTextChannelBehavior

inline fun <reified T> GetTextChannelBehavior.getChannelAsync(id: String): Deferred<T?> {
    val future = CompletableDeferred<T?>()
    RestClient.coroutineScope.launch {
        val data = ChannelCache.getAsync(id).await()
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
