package diskord

import com.github.m5rian.discord.GatewayIntent
import diskord.common.caching.GuildCache
import diskord.common.entities.User
import diskord.common.entities.guild.Guild
import diskord.gateway.listeners.EventListener
import diskord.rest.Endpoints
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flow

object Diskord {
    lateinit var token: String
    lateinit var intents: MutableSet<GatewayIntent>
    val listeners: MutableList<EventListener> = mutableListOf()
    lateinit var id: String

    suspend fun getBotUser(): User = Endpoints.getUser.executeNonNull { arg("user.id", this@Diskord.id) }
    suspend fun getUser(id: String): User? = Endpoints.getUser.execute { arg("user.id", id) }

    fun getGuilds(): Flow<Guild> = flow {
        GuildCache.ids
            .asSequence()
            .mapNotNull { getGuild(it) }
            .forEach { emit(it) }
    }.buffer()

    fun getGuild(id: String): Guild? = GuildCache[id]
}