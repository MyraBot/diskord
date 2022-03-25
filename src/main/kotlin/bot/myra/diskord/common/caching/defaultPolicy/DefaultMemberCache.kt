package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.caching.DoubleKey
import bot.myra.diskord.common.caching.models.MemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member

class DefaultMemberCache {

    val map = mutableMapOf<DoubleKey<String, String>, Member>()

    fun policy(): MemberCachePolicy = MemberCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update {
            val key = DoubleKey(it.guildId, it.id)
            map[key] = it
        }
        remove { map.remove(it) }
    }

}