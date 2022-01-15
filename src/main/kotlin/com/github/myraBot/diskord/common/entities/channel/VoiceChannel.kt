package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.voiceCache
import com.github.myraBot.diskord.rest.request.Promise

data class VoiceChannel(
        override val data: ChannelData,
) : Channel {

    suspend fun getMembers(): Promise<List<Member>> {
        return voiceCache[data.id].map { states ->
            states?.map { it.member.awaitNonNull() } ?: emptyList()
        }
    }

}