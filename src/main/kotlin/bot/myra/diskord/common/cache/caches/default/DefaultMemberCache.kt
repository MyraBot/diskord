package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member

class DefaultMemberCache {

    private val map = mutableMapOf<MemberCacheKey, Member>()

    fun policy(): MemberCachePolicy = MutableMemberCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[MemberCacheKey(it.guildId, it.id)] = it }
        remove { map.remove(MemberCacheKey(it.id.guild, it.id.user)) }
    }

}