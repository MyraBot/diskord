package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.Diskord
import bot.myra.diskord.common.cache.MissingIntentException
import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.GatewayIntent

class DefaultGuildCache {

    init {
        if (GatewayIntent.GUILDS !in Diskord.intents && Diskord.cachePolicy.guild.active) {
            throw MissingIntentException(Diskord.cachePolicy.guild, GatewayIntent.GUILDS)
        }
    }

    private val map = mutableMapOf<String, Guild>()

    fun policy(): GuildCachePolicy = GuildCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}