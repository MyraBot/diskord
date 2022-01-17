package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.VoiceCache
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.runBlocking

data class VoiceChannel(
        override val data: ChannelData,
) : GuildChannel {

    fun getMembers(): Promise<List<Member>> {
        return VoiceCache[data.id].map { states ->
            states?.map {
                it.member.awaitNonNull()
            } ?: emptyList()
        }
    }

    val members: List<Member> get() = runBlocking { getMembers().awaitNonNull() }

}