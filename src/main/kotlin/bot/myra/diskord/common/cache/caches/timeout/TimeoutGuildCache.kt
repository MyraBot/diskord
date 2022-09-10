package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.GuildCachePolicy
import bot.myra.diskord.common.cache.models.MutableGuildCachePolicy
import bot.myra.diskord.common.entities.guild.Guild
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutGuildCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, Guild>(expireIn) {

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
            stopExpiry(it)
            cache.remove(it)
        }
    }

}