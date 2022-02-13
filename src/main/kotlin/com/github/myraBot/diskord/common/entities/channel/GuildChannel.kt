package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.rest.request.promises.Promise

interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    suspend fun getGuild(): Promise<Guild> = Diskord.getGuild(data.guildId.value!!)
}