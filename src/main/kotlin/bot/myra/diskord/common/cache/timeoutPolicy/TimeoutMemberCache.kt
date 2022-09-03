package bot.myra.diskord.common.cache.timeoutPolicy

import bot.myra.diskord.common.cache.DoubleKey
import bot.myra.diskord.common.cache.models.MemberCachePolicy
import bot.myra.diskord.common.cache.models.MutableMemberCachePolicy
import bot.myra.diskord.common.entities.guild.Member
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutMemberCache(expireIn: Duration = 10.seconds) : TimeoutCache<DoubleKey<String, String>, Member>(expireIn) {

    fun policy(): MemberCachePolicy = MutableMemberCachePolicy().apply {
        view { map.values.onEach { it.updateExpiry() }.map { it.value } }
        get { map[it]?.apply { updateExpiry() }?.value }
        update {
            val key = DoubleKey(it.guildId, it.id)
            map[key]?.apply { this.value = it } ?: run { map[key] = TimeoutCacheValue(it) }
        }
        remove { map.remove(it) }
    }

}