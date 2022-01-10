package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.VoiceStateCache
import com.github.myraBot.diskord.common.entities.guild.Member

data class VoiceChannel(
        override val data: ChannelData
) : Channel {

    val members: List<Member> = VoiceStateCache[data.id]?.map { it.member!! } ?: emptyList()

}