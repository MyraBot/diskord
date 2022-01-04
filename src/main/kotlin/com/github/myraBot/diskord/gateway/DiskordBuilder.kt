package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.Events
import com.github.myraBot.diskord.rest.builders.ArgumentBuilder

object DiskordBuilder {
    var token: String = ""
    var listenerPackage: String = ""
    private val listeners: MutableList<EventListener> = mutableListOf()
    private var intents: MutableSet<GatewayIntent> = mutableSetOf(GatewayIntent.GUILDS, GatewayIntent.GUILD_MEMBERS) // Default intents are required for caching
    private val cache: MutableSet<Cache> = mutableSetOf()
    var textTransform: suspend (String, ArgumentBuilder) -> String = { string, _ -> string }

    fun addListeners(vararg listeners: EventListener) {
        this.listeners.addAll(listeners)
    }

    fun intents(vararg intents: GatewayIntent) {
        this.intents.addAll(intents)
    }

    fun cache(vararg cache: Cache) {
        this.cache.addAll(cache)
        this.intents.addAll(this.cache.flatMap { it.intents })
    }

    /**
     * Registers all registered events from [listeners],
     * initialises [Diskord] and starts the websocket.
     *
     * @return Returns the [Diskord] object. Just for laziness.
     */
    suspend fun start() {
        Diskord.apply {
            this.token = this@DiskordBuilder.token
            this.cache = this@DiskordBuilder.cache
        }
        Events.register(listeners, listenerPackage) // Events need to be registered after applying the cache to the Diskord object, so only required listeners get registered
        Websocket.apply { this.intents = this@DiskordBuilder.intents }.connect() // Connect to actual websocket
    }

}

fun discordBot(builder: DiskordBuilder.() -> Unit): DiskordBuilder {
    val bot = DiskordBuilder.apply(builder)
    if (DiskordBuilder.token.isBlank()) throw IllegalArgumentException("Your token is invalid")
    return bot
}