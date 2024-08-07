package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.MutableUserCachePolicy
import bot.myra.diskord.common.cache.models.UserCachePolicy
import bot.myra.diskord.common.entities.user.UserData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutUserCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, UserData>(expireIn) {
    private val cache = mutableMapOf<String,UserData>()

    override fun policy(): UserCachePolicy = MutableUserCachePolicy().apply {
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