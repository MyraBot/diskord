package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.entities.Channel
import com.github.myraBot.diskord.common.entities.guild.Guild

data class VoiceChannel(
        private val data: Channel
) {
    val id: String = data.id
    val guild: Guild? get() = data.guildId?.let { GuildCache[it] }
}