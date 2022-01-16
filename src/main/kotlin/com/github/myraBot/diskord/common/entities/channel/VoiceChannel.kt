package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.voiceCache
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.runBlocking

data class VoiceChannel(
        override val data: ChannelData,
) : GuildChannel {

    fun getMembers(): Promise<List<Member>> {
        return voiceCache[data.id].map { states ->
            states?.map {
                println("retrieving member")
                println("member = ${it.userId}")
                it.member.awaitNonNull()
            } ?: emptyList<Member>().also { println("returning empty list") }
        }
    }

    val members: List<Member> get() = runBlocking { getMembers().awaitNonNull() }

}