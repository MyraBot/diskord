package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.events.ListenTo
import com.github.myraBot.diskord.gateway.events.impl.guild.GuildLoadEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred

object GuildCache : Cache<String, Guild>() {

    override fun retrieveAsync(key: String): Deferred<Guild?> {
        return RestClient.executeAsync(Endpoints.getGuild) {
            arguments { arg("guild.id", key) }
        }
    }

    @ListenTo(GuildLoadEvent::class)
    fun onGuildCreate(event: GuildLoadEvent) {
        Diskord.guildIds.add(event.guild.id)
        cache[event.guild.id] = event.guild
        Diskord.pendingGuilds[event.guild.id]?.complete(event.guild)
    }

}

