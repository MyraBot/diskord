package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import kotlinx.coroutines.Deferred

interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuild(): Deferred<Guild?> = Diskord.getGuild(data.guildId.value!!)
}