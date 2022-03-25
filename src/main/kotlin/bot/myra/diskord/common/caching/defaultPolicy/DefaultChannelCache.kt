package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.caching.models.ChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData

class DefaultChannelCache {

    val map = mutableMapOf<String, ChannelData>()

    fun policy(): ChannelCachePolicy = ChannelCachePolicy().apply {
        view { map.values.toList() }
        get {
            println("Getting from cache")
            map[it]
        }
        update { map[it.id] = it }
        remove { map.remove(it) }
    }

}