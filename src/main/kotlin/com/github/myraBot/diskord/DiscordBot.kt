package com.github.m5rian.discord

import com.github.myraBot.diskord.gateway.Websocket
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.Events

object DiscordBot {
    var token: String = ""
    val listeners: MutableList<EventListener> = mutableListOf()
    var listenerPackage: String = ""
    val intents: MutableList<GatewayIntent> = mutableListOf()

    fun addListeners(vararg listeners: EventListener) {
        this.listeners.addAll(listeners)
    }

    fun intents(vararg intents: GatewayIntent) {
        this.intents.addAll(intents)
    }

    suspend fun start() {
        Events.register()
        Websocket.connect()
    }

}

fun discordBot(builder: DiscordBot.() -> Unit): DiscordBot {
    val bot = DiscordBot.apply(builder)
    if (bot.token.isBlank()) throw IllegalArgumentException("Your token is invalid")
    return bot
}