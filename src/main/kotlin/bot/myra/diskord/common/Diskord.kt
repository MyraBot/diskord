package bot.myra.diskord.common

import bot.myra.diskord.common.Diskord.cachePolicy
import bot.myra.diskord.common.Diskord.gatewayClient
import bot.myra.diskord.common.Diskord.intents
import bot.myra.diskord.common.Diskord.listeners
import bot.myra.diskord.common.caching.CachePolicy
import bot.myra.diskord.common.entities.applicationCommands.slashCommands.SlashCommand
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.message.Message
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.gateway.commands.Presence
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.commands.Status
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.gateway.handler.Gateway
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
import io.ktor.client.plugins.websocket.WebSockets
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.reflect.KFunction
import kotlin.system.exitProcess

/**
 * Main Api interface. Can be used as a direct object reference.
 * **Do not use this object before calling the [diskord] function.**
 *
 * With this object you can access the Discord API without the needs of an event.
 *
 * Holds gateway information like
 * - [gatewayClient]
 * - [listeners]
 * - [intents]
 * - [cachePolicy]
 */
@Suppress("unused")
object Diskord : GetTextChannelBehavior {
    lateinit var token: String
    internal lateinit var gateway: Gateway
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
    fun hasWebsocketConnection(): Boolean = ::gateway.isInitialized && gateway.connected

    suspend fun updatePresence(status: Status, presence: Presence.() -> Unit) {
        val newPresence = Presence(status = status).apply(presence)
        val operation = PresenceUpdate(
            newPresence.since,
            newPresence.activity?.let { listOf(it) } ?: emptyList(),
            newPresence.status,
            newPresence.afk
        )
        gateway.updatePresence(operation)
    }

    suspend fun getApplicationCommands(): List<SlashCommand> = EntityProvider.getApplicationCommands()
    suspend fun getBotUser(): User = EntityProvider.getUserNonNull(this.id)
    suspend fun getUser(id: String): User? = EntityProvider.getUser(id)

    fun getGuilds(): Flow<Guild> = flow {
        val copiedIds = guildIds.toList()
        when (cachePolicy.guildCache.update === null) {
            true  -> copiedIds.forEach { id -> emitGuildFromCache(id) }
            false -> copiedIds.forEach { id -> this@Diskord.getGuild(id)?.let { emit(it) } }
        }
    }

    private suspend fun FlowCollector<Guild>.emitGuildFromCache(id: String) {
        val guild: Guild? = cachePolicy.guildCache.get(id) ?: run {
            pendingGuilds[id]?.await() ?: EntityProvider.getGuild(id)
        }
        guild?.let { emit(it) }
    }

    suspend fun getGuild(id: String): Guild? = EntityProvider.getGuild(id)
    suspend fun fetchGuild(id: String): Guild? = EntityProvider.fetchGuild(id)

    suspend fun getMessage(channel: String, message: String): Message? {
        return RestClient.executeNullable(Endpoints.getChannelMessage) {
            arguments {
                arg("channel.id", channel)
                arg("message.id", message)
            }
        }
    }

    suspend fun getMessages(channel: String, before: String?, max: Int): List<Message> = RestClient.execute(Endpoints.getChannelMessages) {
        arguments { arg("channel.id", channel) }
        queryParameter.add("limit" to max)
        before?.let { queryParameter.add("before" to before) }
    }

}

fun diskord(builder: suspend Diskord.() -> Unit) = CoroutineScope(Dispatchers.Default).launch {
    builder.invoke(Diskord)
    if (Diskord.token.isBlank()) {
        error(Diskord::class) { "Your token is invalid, aborting..." }
        exitProcess(-1)
    }
}