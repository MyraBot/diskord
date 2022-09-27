package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.models.MutableUserCachePolicy
import bot.myra.diskord.common.cache.models.UserCachePolicy
import bot.myra.diskord.common.entities.user.User

class DefaultUserCache {

    private val map = mutableMapOf<String, User>()

    fun policy(): UserCachePolicy = MutableUserCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it.id) }
    }

}