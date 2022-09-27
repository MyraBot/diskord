package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeoutMemberCache(expireIn: Duration = 10.minutes) : TimeoutCache<MemberCacheKey, Member>(expireIn) {
    private val cache = mutableMapOf<MemberCacheKey, Member>()

    override fun policy(): MemberCachePolicy = MutableMemberCachePolicy().apply {
        view {
            cache.keys.forEach { startExpiry(it) }
            cache.values.toList()
        }
        get {
            startExpiry(it)
            cache[it]
        }
        update {
            cache[MemberCacheKey(it.guildId, it.id)] = it
        }
        remove {
            stopExpiry(it.id)
            cache.remove(it.id)
        }
    }

}