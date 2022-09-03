package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member

class DefaultMemberCache {

    private val map = mutableMapOf<DoubleKey<String, String>, Member>()

    fun policy(): MemberCachePolicy = MutableMemberCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update {
            val key = DoubleKey(it.guildId, it.id)
            map[key] = it
        }
        remove { map.remove(it) }
    }

}