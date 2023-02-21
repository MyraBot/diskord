package bot.myra.diskord.common

import bot.myra.diskord.common.cache.CachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.common.entities.guild.GuildData
import bot.myra.diskord.common.entities.user.User
import bot.myra.diskord.common.utilities.FileFormats
import bot.myra.diskord.common.utilities.toJson
import bot.myra.diskord.gateway.Gateway
import bot.myra.diskord.gateway.GatewayIntent
import bot.myra.diskord.gateway.commands.PresenceUpdate
import bot.myra.diskord.gateway.events.EventListener
import bot.myra.diskord.rest.DefaultTransformer
import bot.myra.diskord.rest.Endpoints
import bot.myra.diskord.rest.EntityProvider
import bot.myra.diskord.rest.MessageTransformer
import bot.myra.diskord.rest.bodies.ModifyCurrentUser
import bot.myra.diskord.rest.request.RestClient
import bot.myra.diskord.rest.request.error.ErrorHandler
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.FlowCollector
import org.reflections.Reflections
import org.slf4j.LoggerFactory
import java.util.*
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
class Diskord(
    val token: String
) : EntityProvider {
    override val diskord = this
    lateinit var id: String

    val logger = LoggerFactory.getLogger(Diskord::class.java)
    var gatewayClient = HttpClient(OkHttp) { install(WebSockets) }

    internal lateinit var gateway: Gateway
    val rest: RestClient = RestClient(this)

    val listeners: MutableMap<EventListener, List<KFunction<*>>> = mutableMapOf()
    var intents: MutableSet<GatewayIntent> = mutableSetOf()
    var cachePolicy: CachePolicy = CachePolicy()
    var rateLimitThreshold = 1000
    var errorHandler: ErrorHandler = ErrorHandler()
    var transformer: MessageTransformer = DefaultTransformer

    var initialConnection: Boolean = true
    val guildIds: MutableSet<String> = mutableSetOf()

    /**
     * Stores all unavailable guilds from the ready event
     * until we receive a guild create event, which we can
     * use to refill the guilds' data.
     *
     * So this map contains
     * - Normal unavailable guilds
     * - Shadowed guilds (Guilds which got removed by Discords trust and saftey team)
     *
     * If we receive a guild delete event of such guild and the
     * guild is still in here (so that means that I didn't receive
     * a guild create event) we know that the guild is a shadowed guild,
     * so it got removed by Discords trust and safety team. This is handled
     * in the [bot.myra.diskord.gateway.events.impl.ReadyEvent]
     */
    var unavailableGuilds: MutableMap<String, CompletableDeferred<Guild>> = mutableMapOf()

    init {
        if (token.isBlank()) {
            logger.error("Your token is invalid, aborting...")
            exitProcess(-1)
        }
    }

    fun addListeners(vararg listeners: EventListener) = listeners.forEach {
        this.listeners[it] = it.findEventFunction()
    }

    /**
     * Load listeners by reflection.
     * Finds and loads all listeners in a specific package.
     *
     * @param packages All packages to load the listeners for
     */
    fun addListenersByReflection(vararg packages: String) {
        packages.forEach { packageName ->
            Reflections(packageName).getSubTypesOf(EventListener::class.java)
                .mapNotNull { it.kotlin.objectInstance }
                .forEach { this.listeners[it] = it.findEventFunction() }
        }
    }

    fun intents(vararg intents: GatewayIntent) = this.intents.addAll(intents)
    fun cachePolicy(builder: CachePolicy.() -> Unit) = run { cachePolicy = CachePolicy().apply(builder) }
    fun hasWebsocketConnection(): Boolean = ::gateway.isInitialized && gateway.connected

    suspend fun updatePresence(status: PresenceUpdate.Status, presence: PresenceUpdate.() -> Unit) {
        val newPresence = PresenceUpdate(status = status).apply(presence)
        gateway.updatePresence(newPresence)
    }

    suspend fun updateAvatar(bytes: ByteArray, file: FileFormats): User? {
        val supportedFileTypes = listOf(FileFormats.JPEG, FileFormats.PNG)
        if (file !in supportedFileTypes) throw UnsupportedOperationException("${file.name} isn't supported as avatars")

        val base64 = Base64.getEncoder().encodeToString(bytes)
        val avatarString = "data:image/${file.extension};base64,$base64"
        return diskord.rest.execute(Endpoints.modifyCurrentUser) {
            json = ModifyCurrentUser(getBotUser().username, avatarString).toJson()
        }.value?.let { User(it, this) }
    }

    private suspend fun FlowCollector<Guild>.emitGuildFromCache(id: String) {
        val guild: GuildData? = cachePolicy.guild.get(id).value ?: run {
            unavailableGuilds[id]?.await()?.data ?: getGuild(id).value?.data
        }
        guild?.let { emit(Guild(diskord, it)) }
    }
}