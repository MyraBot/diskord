package bot.myra.diskord.common

import bot.myra.diskord.common.caching.GuildCache
import bot.myra.diskord.common.caching.UserCache
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.commands.Presence
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.commands.Status
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.events.Events
import bot.myra.diskord.gateway.handler.Websocket
import bot.myra.diskord.gateway.handler.intents.Cache
import bot.myra.diskord.gateway.handler.intents.GatewayIntent
import bot.myra.diskord.rest.DefaultTransformer
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.MessageTransformer
import bot.myra.diskord.rest.behaviors.GetTextChannelBehavior
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.error.ErrorHandler
import bot.myra.kommons.error
import bot.myra.kommons.info
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlin.reflect.KFunction
import kotlin.system.exitProcess

@Suppress("unused")
object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    internal lateinit var websocket: Websocket
    lateinit var id: String

    var gatewayClient = HttpClient(CIO) { install(WebSockets) }

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

    val guildIds: MutableSet<String> = mutableSetOf()
    var unavailableGuilds: MutableSet<String> = mutableSetOf()
    val pendingGuilds: MutableMap<String, CompletableDeferred<Guild>> = mutableMapOf()

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

    fun getBotUserAsync(): Deferred<User> = UserCache.getNonNullAsync(this.id)
    fun getUserAsync(id: String): Deferred<User?> = UserCache.getAsync(id)

    fun getGuildsAsync(): Flow<Guild> = flow {
        val copiedIds = guildIds.toList()
        when (cache.contains(Cache.GUILD)) {
            true -> copiedIds.forEach { id -> emitGuildFromCache(id) }
            false -> copiedIds.forEach { id -> this@Diskord.getGuildAsync(id).await()?.let { emit(it) } }
        }
    }

    private suspend fun FlowCollector<Guild>.emitGuildFromCache(id: String) {
        val guild: Guild? = GuildCache.cache[id] ?: run {
            if (unavailableGuilds.contains(id)) {
                pendingGuilds[id] = CompletableDeferred()
                pendingGuilds[id]?.await()
            } else GuildCache.retrieveAsync(id).await()
        }
        guild?.let { emit(it) }
    }

    fun getGuildAsync(id: String): Deferred<Guild?> = GuildCache.getAsync(id)

    fun getMessageAsync(channel: String, message: String): Deferred<Message?> {
        return RestClient.executeNullableAsync (Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channel)
                arg("message.id", message)
            }
        }
    }

}

fun diskord(builder: suspend Diskord.() -> Unit) = CoroutineScope(Dispatchers.Default).launch {
    builder.invoke(Diskord)
    if (Diskord.token.isBlank()) {
        error(Diskord::class) { "Your token is invalid, aborting..." }
        exitProcess(-1)
    }
}