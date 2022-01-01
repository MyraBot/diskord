package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.Events
import com.github.myraBot.diskord.rest.builders.ArgumentBuilder

object DiskordBuilder {
    var token: String = ""
    private val listeners: MutableList<EventListener> = mutableListOf()
    var listenerPackage: String = ""
    private var intents: MutableSet<GatewayIntent> = mutableSetOf(GatewayIntent.GUILDS, GatewayIntent.GUILD_MEMBERS) // Default intents are required for caching
    var textTransform: suspend (String, ArgumentBuilder) -> String = { string, _ -> string }

    fun addListeners(vararg listeners: EventListener) {
        DiskordBuilder.listeners.addAll(listeners)
    }

    fun intents(vararg intents: GatewayIntent) {
        DiskordBuilder.intents.addAll(intents)
    }

    /**
     * Registers all registered events from [listeners],
     * initialises [Diskord] and starts the websocket.
     *
     * @return Returns the [Diskord] object. Just for laziness.
     */
    suspend fun start(): Diskord {
        Events.register(listenerPackage, listeners)

        return Diskord.apply {
            DiskordBuilder.token = DiskordBuilder.token
            DiskordBuilder.intents = DiskordBuilder.intents

            Websocket.connect() // Connect to actual websocket
        }
    }

}

fun discordBot(builder: DiskordBuilder.() -> Unit): DiskordBuilder {
    val bot = DiskordBuilder.apply(builder)
    if (DiskordBuilder.token.isBlank()) throw IllegalArgumentException("Your token is invalid")
    return bot
}