package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.MemberCacheKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.MemberData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeoutMemberCache(expireIn: Duration = 10.minutes) : TimeoutCache<MemberCacheKey, MemberData>(expireIn) {
    private val cache = mutableMapOf<MemberCacheKey, MemberData>()

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
            cache[MemberCacheKey(it.guildId, it.user.id)] = it
        }
        remove {
            stopExpiry(it.id)
            cache.remove(it.id)
        }
    }

}