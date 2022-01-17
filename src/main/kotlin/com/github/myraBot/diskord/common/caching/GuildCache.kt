package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.ListenTo
import com.github.myraBot.diskord.gateway.listeners.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow

object GuildCache : Cache<String, Guild>(
    retrieve = { key ->
        Promise.of(Endpoints.getGuild) { arg("guild.id", key) }
    }
) {
    val ids: MutableList<String> = mutableListOf()

    fun getAll(): Flow<Guild> = flow {
        val copiedIds = ids.toList()
        copiedIds.forEach { id ->
            println(id)
            cache[id]
                ?.run { emit(this) }
            ?: emit(Diskord.getGuild(id).awaitNonNull())

        }
    }.buffer()

    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) {
        ids.remove(event.guild.id)
        cache[event.guild.id] = event.guild
    }

}

