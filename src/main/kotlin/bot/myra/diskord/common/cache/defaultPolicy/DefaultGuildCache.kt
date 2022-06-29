package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.entities.guild.Guild

class DefaultGuildCache {

    private val map = mutableMapOf<String, Guild>()

    fun policy(): GuildCachePolicy = GuildCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}