package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.VoiceCache
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.*

data class VoiceChannel(
    override val data: ChannelData,
) : GuildChannel {

    fun getMembersAsync(): Deferred<List<Member>> {
        val future = CompletableDeferred<List<Member>>()
        RestClient.coroutineScope.launch {
            val voiceStates = VoiceCache.get(data.id).await()
            val members = voiceStates?.map { state ->
                state.getMemberAsync()
            }?.awaitAll() ?: emptyList()
            future.complete(members)
        }
        return future
    }

}