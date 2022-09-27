package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.MessageCachePolicy
import bot.myra.diskord.common.cache.models.MutableMessageCachePolicy
import bot.myra.diskord.common.entities.message.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeoutMessageCache(expireIn: Duration = 10.minutes) : TimeoutCache<String, Message>(expireIn) {
    private val cache = mutableMapOf<String, Message>()

    override fun policy(): MessageCachePolicy = MutableMessageCachePolicy().apply {
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