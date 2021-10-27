package com.github.myraBot.diskord

import com.github.m5rian.discord.GatewayIntent
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.Endpoints

object Diskord {
    lateinit var token: String
    lateinit var intents: MutableList<GatewayIntent>
    val listeners: MutableList<EventListener> = mutableListOf()
    lateinit var id: String

    suspend fun getBotUser(): User = User(Endpoints.getUser.execute { arg("user.id", this@Diskord.id) })
    suspend fun getUser(id: String): User = User(Endpoints.getUser.execute { arg("user.id", id) })
}