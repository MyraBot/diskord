package com.github.myraBot.diskord.common.cache

import com.github.myraBot.diskord.common.entities.guild.Guild

object GuildCache {
    var ids: MutableList<String> = mutableListOf()
    var guilds: MutableList<Guild> = mutableListOf()
}