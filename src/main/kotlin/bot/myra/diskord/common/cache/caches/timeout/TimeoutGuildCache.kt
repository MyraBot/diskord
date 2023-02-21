package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.cache.models.MutableGuildCachePolicy
import bot.myra.diskord.common.entities.guild.GuildData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Suppress("unused")
class TimeoutGuildCache(expireIn: Duration = 10.minutes) : TimeoutCache<String, GuildData>(expireIn) {
    private val cache = mutableMapOf<String, GuildData>()

    override fun policy(): GuildCachePolicy = MutableGuildCachePolicy().apply {
        view {
            cache.keys.forEach { startExpiry(it) }
            cache.values.toList()
        }
        get {
            startExpiry(it)
            cache[it]
        }
        update {
            cache[it.id] = it
        }
        remove {
            stopExpiry(it.id)
            cache.remove(it.id)
        }
    }

}