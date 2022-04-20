package bot.myra.diskord.common

import bot.myra.diskord.common.caching.CachePolicy
import bot.myra.diskord.common.entities.User
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.gateway.commands.Presence
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.commands.Status
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.handler.Websocket
import bot.myra.diskord.gateway.handler.intents.GatewayIntent
import bot.myra.diskord.rest.DefaultTransformer
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.MessageTransformer
import bot.myra.diskord.rest.behaviors.GetTextChannelBehavior
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.error.ErrorHandler
import bot.myra.kommons.error
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction
import kotlin.system.exitProcess

@Suppress("unused")
object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    internal lateinit var websocket: Websocket
    lateinit var id: String

    var gatewayClient = HttpClient(CIO) { install(WebSockets) }
    var listenersPackage: List<String> = emptyList()
    val listeners: MutableMap<EventListener, List<KFunction<*>>> = mutableMapOf()
    var intents: MutableSet<GatewayIntent> = mutableSetOf()
    var cachePolicy: CachePolicy = CachePolicy()
        set(value) {
            field = value

            value.userCache.loadListeners()
            value.guildCache.loadListeners()
            value.memberCache.loadListeners()
            value.voiceStateCache.loadListeners()
            value.channelCache.loadListeners()
            value.roleCache.loadListeners()
        }
    var rateLimitThreshold = 1000
    var errorHandler: ErrorHandler = ErrorHandler()
    var transformer: MessageTransformer = DefaultTransformer

    val guildIds: MutableSet<String> = mutableSetOf()
    var unavailableGuilds: MutableSet<String> = mutableSetOf()
    val pendingGuilds: MutableMap<String, CompletableDeferred<Guild>> = mutableMapOf()

    fun addListeners(vararg listeners: EventListener) = listeners.forEach(EventListener::loadListeners)
    fun intents(vararg intents: GatewayIntent) = this.intents.addAll(intents)
    fun cachePolicy(builder: CachePolicy.() -> Unit) = run { cachePolicy = CachePolicy().apply(builder) }
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

    suspend fun getBotUser():User = EntityProvider.getUserNonNull(this.id)
    suspend fun getUser(id: String):User? = EntityProvider.getUser(id)

    fun getGuilds(): Flow<Guild> = flow {
        val copiedIds = guildIds.toList()
        when (cachePolicy.guildCache.update === null) {
            true -> copiedIds.forEach { id -> emitGuildFromCache(id) }
            false -> copiedIds.forEach { id -> this@Diskord.getGuild(id)?.let { emit(it) } }
        }
    }

    private suspend fun FlowCollector<Guild>.emitGuildFromCache(id: String) {
        val guild: Guild? = cachePolicy.guildCache.get(id) ?: run {
            if (unavailableGuilds.contains(id)) {
                pendingGuilds[id]!!.await()
            } else EntityProvider.getGuild(id)
        }
        guild?.let { emit(it) }
    }

    suspend fun getGuild(id: String):Guild? = EntityProvider.getGuild(id)
    suspend fun fetchGuild(id: String):Guild? = EntityProvider.fetchGuild(id)

    suspend fun getMessage(channel: String, message: String):Message? {
        return RestClient.executeNullable(Endpoints.getChannelMessage) {
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