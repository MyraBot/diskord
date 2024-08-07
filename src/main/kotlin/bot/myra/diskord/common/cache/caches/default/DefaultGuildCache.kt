package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.cache.models.MutableGuildCachePolicy
import bot.myra.diskord.common.entities.guild.GuildData

class DefaultGuildCache {

    private val map = mutableMapOf<String, GuildData>()

    fun policy(): GuildCachePolicy = MutableGuildCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it.id) }
    }

}