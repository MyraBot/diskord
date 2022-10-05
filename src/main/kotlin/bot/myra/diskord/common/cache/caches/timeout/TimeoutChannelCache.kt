package bot.myra.diskord.common.cache.caches.timeout

import bot.myra.diskord.common.cache.models.ChannelCachePolicy
import bot.myra.diskord.common.cache.models.MutableChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

class TimeoutChannelCache(expireIn: Duration = 10.minutes) : TimeoutCache<String, ChannelData>(expireIn) {
    private val cache = mutableMapOf<String, ChannelData>()
    private val guildMap = mutableMapOf<String, MutableList<ChannelData>>()

    override fun policy(): ChannelCachePolicy = MutableChannelCachePolicy().apply {
        view {
            cache.values.onEach { startExpiry(it.id) }.toList()
        }
        get {
            startExpiry(it)
            cache[it]
        }
        update {
            cache[it.id] = it
        }
        remove {
            stopExpiry(it.id)
            cache.remove(it.id)
        }

        guildView {
            guildMap[it] ?: emptyList()
        }
        guildUpdate {
            guildMap.getOrPut(it.guild) { mutableListOf() }.apply {
                removeIf { channel -> channel.id == it.value.id }
                add(it.value)
            }
        }
        guildRemove {
            stopExpiry(it.guild)
            guildMap[it.guild]?.removeIf { channel -> channel.id == it.id }
        }
    }

}