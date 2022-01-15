package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.rest.request.Promise

interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    fun getGuild(): Promise<Guild> = Diskord.getGuild(data.guildId.value!!)
}