package com.github.myraBot.diskord.common

import bot.myra.kommons.error
import bot.myra.kommons.info
import com.github.myraBot.diskord.common.caching.GuildCache
import com.github.myraBot.diskord.common.caching.UserCache
import com.github.myraBot.diskord.common.entities.User
import com.github.myraBot.diskord.common.entities.guild.Guild
import com.github.myraBot.diskord.gateway.commands.Presence
import com.github.myraBot.diskord.gateway.commands.PresenceUpdate
import com.github.myraBot.diskord.gateway.commands.Status
import com.github.myraBot.diskord.gateway.events.EventListener
import com.github.myraBot.diskord.gateway.events.Events
import com.github.myraBot.diskord.gateway.handler.Websocket
import com.github.myraBot.diskord.gateway.handler.intents.Cache
import com.github.myraBot.diskord.gateway.handler.intents.GatewayIntent
import com.github.myraBot.diskord.rest.DefaultTransformer
import com.github.myraBot.diskord.rest.MessageTransformer
import com.github.myraBot.diskord.rest.behaviors.GetTextChannelBehavior
import com.github.myraBot.diskord.rest.request.error.ErrorHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
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
    var errorHandler: ErrorHandler = ErrorHandler()
    var transformer: MessageTransformer = DefaultTransformer

    val unavailableGuilds: MutableList<String> = mutableListOf()

    fun addListeners(vararg listeners: EventListener) = listeners.forEach(EventListener::loadListeners)
    fun intents(vararg intents: GatewayIntent) = this.intents.addAll(intents)

    fun cache(vararg cache: Cache) {
        this.cache.addAll(cache)
        this.intents.addAll(this.cache.flatMap { it.intents })
    }

    fun hasWebsocketConnection(): Boolean = ::websocket.isInitialized && websocket.connected

    suspend fun updatePresence(status: Status, presence: Presence.() -> Unit) {
        val newPresence = Presence(status = status).apply(presence)
        val operation = PresenceUpdate(
            newPresence.since,
            newPresence.activity?.let { listOf(it) } ?: emptyList(),
            newPresence.status,
            newPresence.afk
        )
        websocket.updatePresence(operation)
    }

    fun getBotUser(): Deferred<User?> = UserCache.get(this.id)
    fun getUser(id: String): Deferred<User?> = UserCache.get(id)

    fun getGuilds(): Flow<Guild> = GuildCache.getAll()
    fun getGuild(id: String): Deferred<Guild?> = GuildCache.get(id)
}

fun diskord(builder: suspend Diskord.() -> Unit) = CoroutineScope(Dispatchers.Default).launch {
    builder.invoke(Diskord)
    if (Diskord.token.isBlank()) {
        error(Diskord::class) { "Your token is invalid, aborting..." }
        exitProcess(-1)
    }
}