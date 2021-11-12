package com.github.myraBot.diskord

import com.github.m5rian.discord.GatewayIntent
import com.github.myraBot.diskord.common.cache.GuildCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.Endpoints
import kotlinx.coroutines.runBlocking

object Diskord {
    lateinit var token: String
    lateinit var intents: MutableList<GatewayIntent>
    val listeners: MutableList<EventListener> = mutableListOf()
    lateinit var id: String

    suspend fun getBotUser(): User = Endpoints.getUser.executeNonNull { arg("user.id", this@Diskord.id) }
    suspend fun getUser(id: String): User? = Endpoints.getUser.execute { arg("user.id", id) }

    suspend fun getGuilds() = GuildCache.ids
        .asSequence()
        .mapNotNull { runBlocking { getGuild(it) } }
        .toList()
        .also { GuildCache.guilds = it.toMutableList() }
    suspend fun getGuild(id: String): Guild? = Endpoints.getGuild.execute { arg("guild.id", id) }
}