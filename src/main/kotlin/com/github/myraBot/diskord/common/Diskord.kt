package com.github.myraBot.diskord.common

import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.Cache
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.Promise

object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    val listeners: MutableList<EventListener> = mutableListOf()
    var cache: MutableSet<Cache> = mutableSetOf()
    lateinit var id: String
    val guildIds: MutableList<String> = mutableListOf()

    fun getBotUser(): Promise<User> = userCache[this.id]
    fun getUser(id: String): Promise<User> = userCache[id]

    fun getGuilds(): List<Guild> = guildCache.collect()
    fun getGuild(id: String): Promise<Guild> = guildCache[id]
}