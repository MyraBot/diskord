package bot.myra.diskord.common.caching.defaultPolicy

import bot.myra.diskord.common.Diskord.id
import bot.myra.diskord.common.caching.models.ChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData

class DefaultChannelCache {

    val map = mutableMapOf<String, ChannelData>()
    val guildMap = mutableMapOf<String, MutableList<ChannelData>>()

    fun policy(): ChannelCachePolicy = ChannelCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
        guildAssociation.apply {
            viewOrNull { guildMap[it] }
            associatedByGuild { view().firstOrNull { it.id == id }?.guildId?.value }
            update { channel ->
                val guild = channel.guildId.value ?: return@update
                val guildCache = guildMap.getOrPut(guild) { mutableListOf() }
                guildCache.removeIf { it.id == channel.id }
                guildCache.add(channel)
            }
            remove { pair ->
                val guildCache = guildMap[pair.first] ?: return@remove
                guildCache.removeIf { it.id == pair.second }
            }
        }
    }

}