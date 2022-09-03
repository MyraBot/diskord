package bot.myra.diskord.common.cache.timeoutPolicy

import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.cache.models.MutableGuildCachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutGuildCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, Guild>(expireIn) {

    fun policy(): GuildCachePolicy = MutableGuildCachePolicy().apply {
        view { map.values.onEach { it.updateExpiry() }.map { it.value } }
        get { map[it]?.apply { updateExpiry() }?.value }
        update { map[it.id]?.apply { this.value = it } ?: run { map[it.id] = TimeoutCacheValue(it) } }
        remove { map.remove(it) }
    }

}