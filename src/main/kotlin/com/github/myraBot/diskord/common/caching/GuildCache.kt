package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking


object GuildCache : Cache<String, Guild>() {

    var ids: MutableList<String> = mutableListOf()

    override fun retrieve(key: String): Guild? {
        return runBlocking {
            Endpoints.getGuild.execute { arg("guild.id", key) }
        }
    }

    override fun update(value: Guild) {
        println("Updating channel $value")
        map[value.id] = value
    }
}