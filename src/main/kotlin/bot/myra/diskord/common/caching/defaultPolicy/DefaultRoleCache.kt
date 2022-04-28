package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.caching.models.RoleCachePolicy
import bot.myra.diskord.common.entities.guild.Role

class DefaultRoleCache {

    private val map = mutableMapOf<String, Role>()

    fun policy(): RoleCachePolicy = RoleCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}