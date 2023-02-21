package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.models.MessageCachePolicy
import bot.myra.diskord.common.cache.models.MutableMessageCachePolicy
import bot.myra.diskord.common.entities.message.MessageData

class DefaultMessageCache {

    private val map = mutableMapOf<String, MessageData>()

    fun policy(): MessageCachePolicy = MutableMessageCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it.id) }
    }

}