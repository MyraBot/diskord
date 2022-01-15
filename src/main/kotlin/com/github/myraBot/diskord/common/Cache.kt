package com.github.myraBot.diskord.common

import com.github.myraBot.diskord.common.entities.Role
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.channel.ChannelData
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.entities.guild.Member
import com.github.myraBot.diskord.common.entities.guild.voice.VoiceState
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.Endpoints
import com.github.myraBot.diskord.rest.request.Promise

data class DoubleKey(val first: String, val second: String)

fun <K, V> cache(cache: Cache<K, V>.() -> Unit) = Cache<K, V>().apply(cache)
class Cache<K, V>(
        var retrieve: (K) -> Promise<V> = { Promise.of(null) },
) : EventListener() {
    private val cache: MutableMap<K, V> = mutableMapOf()

    operator fun get(key: K): Promise<V> {
        return cache[key]?.let { Promise.of(it) } ?: retrieve.invoke(key)
    }

    fun collect(): List<V> = this.cache.values.toList()

}


val userCache = cache<String, User> {
    retrieve = { key ->
        Promise.of(Endpoints.getUser) { arg("user.id", key) }
    }
}
val guildCache = cache<String, Guild> {
    retrieve = { key ->
        Promise.of(Endpoints.getGuild) { arg("guild.id", key) }
    }
}
val memberCache = cache<DoubleKey, Member> {
    retrieve = { key ->
        Promise.of(Endpoints.getGuildMember) {
            arg("user.id", key.first)
            arg("guild.id", key.second)
        }.map { data -> data?.let { Member.withUserInMember(it, key.second) } }
    }
}
val voiceCache = cache<String, List<VoiceState>> {
    retrieve = { Promise.of(null) }
}
val roleCache = cache<DoubleKey, Role> {
    retrieve = { key ->
        Promise.of(Endpoints.getRoles) {
            arg("guild.id", key.second)
        }.map { roles -> roles?.let { role -> role.first { it.id == key.first } } }
    }
}
val channelCache = cache<String, ChannelData> {
    retrieve = { key ->
        Promise.of(Endpoints.getChannel) { arg("channel.id", key) }
    }
}