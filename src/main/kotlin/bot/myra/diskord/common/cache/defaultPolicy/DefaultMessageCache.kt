package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.cache.models.MessageCachePolicy
import bot.myra.diskord.common.entities.message.Message

class DefaultMessageCache {

    private val map = mutableMapOf<String, Message>()

    fun policy(): MessageCachePolicy = MessageCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}