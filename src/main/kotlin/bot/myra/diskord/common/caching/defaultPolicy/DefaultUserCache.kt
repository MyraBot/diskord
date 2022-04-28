package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.caching.models.UserCachePolicy
import bot.myra.diskord.common.entities.user.User

class DefaultUserCache {

    private val map = mutableMapOf<String, User>()

    fun policy(): UserCachePolicy = UserCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}