package com.github.myraBot.diskord.common.entities.channel

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild

interface GuildChannel : Channel {
    val name: String get() = data.name.value!!

    val guild: Guild get() = Diskord.getGuild(data.guildId.value!!)!!
}