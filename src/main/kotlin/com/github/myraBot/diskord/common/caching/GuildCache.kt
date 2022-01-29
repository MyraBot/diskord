package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow

object GuildCache : Cache<String, Guild>(
    retrieve = { key ->
        Promise.of(Endpoints.getGuild) { arg("guild.id", key) }
    }
) {
    val ids: MutableSet<String> = mutableSetOf()

    fun getAll(): Flow<Guild> = channelFlow {
        val copiedIds = ids.toList()
        copiedIds.forEach { id ->
            cache[id]?.run { send(this) } ?: coroutineScope {
                Diskord.getGuild(id).async(this) { send(it!!) }
            }
        }
    }.buffer()

    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) {
        ids.add(event.guild.id)
        cache[event.guild.id] = event.guild
    }

}

