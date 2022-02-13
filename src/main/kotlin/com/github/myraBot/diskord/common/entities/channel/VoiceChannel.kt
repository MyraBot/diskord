package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.VoiceCache
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.coroutines.runBlocking

data class VoiceChannel(
    override val data: ChannelData,
) : GuildChannel {

    suspend fun getMembers(): Promise<List<Member>> {
        return VoiceCache.get(data.id).map { states ->
            states?.map {
                it.getMember().awaitNonNull()
            } ?: emptyList()
        }
    }

    val members: List<Member> get() = runBlocking { getMembers().awaitNonNull() }

}