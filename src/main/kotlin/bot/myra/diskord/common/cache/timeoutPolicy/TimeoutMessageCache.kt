package bot.myra.diskord.common.cache.timeoutPolicy

import bot.myra.diskord.common.cache.models.MessageCachePolicy
import bot.myra.diskord.common.cache.models.MutableMessageCachePolicy
import bot.myra.diskord.common.entities.message.Message
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutMessageCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, Message>(expireIn) {

    override fun policy(): MessageCachePolicy = MutableMessageCachePolicy().apply {
        view { map.values.onEach { it.updateExpiry() }.map { it.value } }
        get { map[it]?.apply { updateExpiry() }?.value }
        update { map[it.id]?.apply { this.value = it } ?: run { map[it.id] = TimeoutCacheValue(it) } }
        remove { map.remove(it) }
    }

}