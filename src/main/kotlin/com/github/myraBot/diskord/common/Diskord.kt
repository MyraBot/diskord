package com.github.myraBot.diskord.common

import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.caching.UserCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.common.utilities.info
import com.github.myraBot.diskord.common.utilities.kError
import com.github.myraBot.diskord.common.utilities.kInfo
import com.github.myraBot.diskord.gateway.Cache
import com.github.myraBot.diskord.gateway.GatewayIntent
import com.github.myraBot.diskord.gateway.Websocket
import com.github.myraBot.diskord.gateway.listeners.EventListener
import com.github.myraBot.diskord.gateway.listeners.Events
import com.github.myraBot.diskord.rest.DefaultErrorHandler
import com.github.myraBot.diskord.rest.DefaultTransformer
import com.github.myraBot.diskord.rest.ErrorHandler
import com.github.myraBot.diskord.rest.MessageTransformer
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.promises.Promise
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KFunction
import kotlin.system.exitProcess

object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    internal lateinit var websocket: Websocket
    lateinit var id: String

    var listenersPackage: String? = null
        set(value) {
            info(this::class) { "Registering discord event listeners" }
            field = value
            value?.let { Events.findListeners(it) }
        }
    val listeners: MutableMap<EventListener, List<KFunction<*>>> = mutableMapOf()
    var intents: MutableSet<GatewayIntent> = mutableSetOf()
    var cache: MutableSet<Cache> = mutableSetOf()
    var errorHandler: ErrorHandler = DefaultErrorHandler
    var transformer: MessageTransformer = DefaultTransformer

    fun addListeners(vararg listeners: EventListener) = listeners.forEach(EventListener::loadListeners)
    fun intents(vararg intents: GatewayIntent) = this.intents.addAll(intents)

    fun cache(vararg cache: Cache) {
        this.cache.addAll(cache)
        this.intents.addAll(this.cache.flatMap { it.intents })
    }

    fun hasWebsocketConnection(): Boolean = ::websocket.isInitialized


    fun getBotUser(): Promise<User> = UserCache[this.id]
    fun getUser(id: String): Promise<User> = UserCache[id]

    fun getGuilds(): Flow<Guild> = GuildCache.getAll()
    fun getGuild(id: String): Promise<Guild> = GuildCache[id]
}

suspend fun diskord(builder: suspend Diskord.() -> Unit) {
    builder.invoke(Diskord)
    if (Diskord.token.isBlank()) {
        kError(Diskord::class) { "Your token is invalid, aborting..." }
        exitProcess(-1)
    }
}