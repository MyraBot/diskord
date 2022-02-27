package com.github.myraBot.diskord.common.caching

import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.events.ListenTo
import com.github.myraBot.diskord.gateway.events.impl.guild.GuildCreateEvent
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.RestClient
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

object GuildCache : Cache<String, Guild>() {
    val ids: MutableSet<String> = mutableSetOf()

    fun getAll(): Flow<Guild> = channelFlow {
        val copiedIds = ids.toList()
        copiedIds.forEach { id ->
            cache[id]?.let { send(it) } ?: RestClient.coroutineScope.launch {
                retrieveAsync(id).await()?.let { send(it) }
            }
        }
    }.buffer()

    override fun retrieveAsync(key: String): Deferred<Guild?> {
        return RestClient.executeAsync(Endpoints.getGuild) {
            arguments { arg("guild.id", key) }
        }
    }

    @ListenTo(GuildCreateEvent::class)
    fun onGuildCreate(event: GuildCreateEvent) {
        ids.add(event.guild.id)
        cache[event.guild.id] = event.guild
    }

}

