package bot.myra.diskord.common.cache.caches.default

import bot.myra.diskord.common.cache.models.ChannelCachePolicy
import bot.myra.diskord.common.cache.models.MutableChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData

class DefaultChannelCache {

    private val map = mutableMapOf<String, ChannelData>()
    private val guildMap = mutableMapOf<String, MutableList<ChannelData>>()

    fun policy(): ChannelCachePolicy = MutableChannelCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it.id) }

        guildView { guildMap[it]?.toList() ?: emptyList() }
        guildUpdate {
            guildMap.getOrPut(it.guild) { mutableListOf() }.apply {
                removeIf { channel -> channel.id == it.value.id }
                add(it.value)
            }
        }
        guildRemove { guildMap[it.guild]?.removeIf { channel -> channel.id == it.id } }
    }

}