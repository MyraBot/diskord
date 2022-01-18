package com.github.myraBot.diskord.common

import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.caching.UserCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.Cache
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.DefaultTransformer
import com.github.myraBot.diskord.rest.MessageTransformer
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.Promise
import kotlinx.coroutines.flow.Flow

object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    lateinit var id: String
    val listeners: MutableList<EventListener> = mutableListOf()
    var cache: MutableSet<Cache> = mutableSetOf()
    var transformer: MessageTransformer = DefaultTransformer

    fun getBotUser(): Promise<User> = UserCache[this.id]
    fun getUser(id: String): Promise<User> = UserCache[id]

    fun getGuilds(): Flow<Guild> = GuildCache.getAll()
    fun getGuild(id: String): Promise<Guild> = GuildCache[id]
}