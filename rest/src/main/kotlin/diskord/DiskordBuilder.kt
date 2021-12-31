package diskord

import com.github.m5rian.discord.GatewayIntent
import diskord.gateway.Websocket
import diskord.gateway.listeners.EventListener
import diskord.gateway.listeners.Events
import diskord.rest.builders.ArgumentBuilder

object DiskordBuilder {
    var token: String = ""
    private val listeners: MutableList<EventListener> = mutableListOf()
    var listenerPackage: String = ""
    private val intents: MutableSet<GatewayIntent> = mutableSetOf(GatewayIntent.GUILDS, GatewayIntent.GUILD_MEMBERS) // Default intents are required for caching
    var textTransform: suspend (String, ArgumentBuilder) -> String = { string, _ -> string }

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