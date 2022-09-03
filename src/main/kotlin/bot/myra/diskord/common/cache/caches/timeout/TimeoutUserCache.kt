package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.MutableUserCachePolicy
import bot.myra.diskord.common.cache.models.UserCachePolicy
import bot.myra.diskord.common.entities.user.User
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutUserCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, User>(expireIn) {

    override fun policy(): UserCachePolicy = MutableUserCachePolicy().apply {
        view { map.values.onEach { it.updateExpiry() }.map { it.value } }
        get { map[it]?.apply { updateExpiry() }?.value }
        update { map[it.id]?.apply { this.value = it } ?: run { map[it.id] = TimeoutCacheValue(it) } }
        remove { map.remove(it) }
    }

}