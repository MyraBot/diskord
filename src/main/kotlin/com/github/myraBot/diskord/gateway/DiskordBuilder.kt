package com.github.myraBot.diskord.gateway

import com.github.myraBot.diskord.common.Diskord
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.rest.DefaultTransformer
import com.github.myraBot.diskord.rest.MessageTransformer

object DiskordBuilder {
    var token: String = ""
    var listenerPackage: String = ""
    internal val listeners: MutableList<EventListener> = mutableListOf()
    internal var intents: MutableSet<GatewayIntent> = mutableSetOf()
    internal val cache: MutableSet<Cache> = mutableSetOf()
    var transformer: MessageTransformer = DefaultTransformer

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

    fun build() {
        Diskord.apply {
            this.token = this@DiskordBuilder.token
            this.cache = this@DiskordBuilder.cache
            this.transformer = this@DiskordBuilder.transformer
        }
    }
}

fun discordBot(builder: DiskordBuilder.() -> Unit): DiskordBuilder {
    val bot = DiskordBuilder.apply(builder)
    if (DiskordBuilder.token.isBlank()) throw IllegalArgumentException("Your token is invalid")
    return bot
}