package bot.myra.diskord.common.cache.defaultPolicy

import bot.myra.diskord.common.Diskord.id
import bot.myra.diskord.common.cache.models.ChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData

class DefaultChannelCache {

    private val map = mutableMapOf<String, ChannelData>()
    private val guildMap = mutableMapOf<String, MutableList<ChannelData>>()

    fun policy(): ChannelCachePolicy = ChannelCachePolicy().apply {
        view { map.values.toList() }
        get { map[it] }
        update { map[it.id] = it }
        remove { map.remove(it) }
        guildAssociation.apply {
            viewOrNull { guildMap[it] }
            associatedByGuild { view().find { it.id == id }?.guildId?.value }
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