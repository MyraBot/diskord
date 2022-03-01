package bot.myra.diskord.common.entities.channel

import bot.myra.diskord.common.caching.VoiceCache
import bot.myra.diskord.common.entities.guild.Member
import bot.myra.diskord.rest.request.RestClient
import kotlinx.coroutines.*

data class VoiceChannel(
    override val data: ChannelData,
) : GuildChannel {

    fun getMembersAsync(): Deferred<List<Member>> {
        val future = CompletableDeferred<List<Member>>()
        RestClient.coroutineScope.launch {
            val voiceStates = VoiceCache.getAsync(data.id).await()
            val members = voiceStates?.map { state ->
                state.getMemberAsync()
            }?.awaitAll() ?: emptyList()
            future.complete(members)
        }
        return future
    }

}