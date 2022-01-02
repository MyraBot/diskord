package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState

data class VoiceChannel(
        private val data: Channel,
        val members: List<VoiceState>
) {
    val id: String = data.id
    val name: String = data.name
    val guild: Guild? get() = data.guildId?.let { GuildCache[it] }
}