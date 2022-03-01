package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import kotlinx.coroutines.Deferred

@Suppress("unused")
interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuildAsync(): Deferred<Guild?> = Diskord.getGuildAsync(data.guildId.value!!)
}