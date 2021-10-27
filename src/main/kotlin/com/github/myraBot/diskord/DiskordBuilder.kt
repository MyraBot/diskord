package com.github.myraBot.diskord

import com.github.m5rian.discord.GatewayIntent
import com.github.myraBot.diskord.gateway.Websocket
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.Events

object DiskordBuilder {
    var token: String = ""
    private val listeners: MutableList<EventListener> = mutableListOf()
    var listenerPackage: String = ""
    private val intents: MutableList<GatewayIntent> = mutableListOf()

    fun addListeners(vararg listeners: EventListener) {
        this.listeners.addAll(listeners)
    }

    fun intents(vararg intents: GatewayIntent) {
        this.intents.addAll(intents)
    }

    /**
     * Registers all registered events from [listeners],
     * initialises [Diskord] and starts the websocket.
     *
     * @return Returns the [Diskord] object. Just for laziness.
     */
    suspend fun start(): Diskord {
        Events.register(this.listenerPackage, this.listeners)

        return Diskord.apply {
            this.token = this@DiskordBuilder.token
            this.intents = this@DiskordBuilder.intents

            Websocket.connect() // Connect to actual websocket
        }
    }

}

fun discordBot(builder: DiskordBuilder.() -> Unit): DiskordBuilder {
    val bot = DiskordBuilder.apply(builder)
    if (bot.token.isBlank()) throw IllegalArgumentException("Your token is invalid")
    return bot
}