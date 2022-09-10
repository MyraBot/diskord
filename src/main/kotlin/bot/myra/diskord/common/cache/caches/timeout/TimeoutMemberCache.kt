package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutMemberCache(expireIn: Duration = 10.seconds) : TimeoutCache<DoubleKey<String, String>, Member>(expireIn) {

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
            cache[DoubleKey(it.guildId, it.id)] = it
        }
        remove {
            stopExpiry(it)
            cache.remove(it)
        }
    }

}