package bot.myra.diskord.common.cache.timeoutPolicy

import bot.myra.diskord.common.cache.models.ChannelCachePolicy
import bot.myra.diskord.common.cache.models.MutableChannelCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TimeoutChannelCache(expireIn: Duration = 10.seconds) : TimeoutCache<String, ChannelData>(expireIn) {

    private val guildMap = mutableMapOf<String, MutableList<ChannelData>>()

    override fun policy(): ChannelCachePolicy = MutableChannelCachePolicy().apply {
        view { map.values.onEach { it.updateExpiry() }.map { it.value } }
        get { map[it]?.apply { updateExpiry() }?.value }
        update { map[it.id]?.apply { this.value = it } ?: run { map[it.id] = TimeoutCacheValue(it) } }
        remove { map.remove(it) }

        guildAssociation.apply {
            viewOrNull { guildId -> guildMap[guildId] }
            associatedByGuild { channelId -> view().find { it.id == channelId }?.guildId?.value }
            update { channel ->
                val guild = channel.guildId.value ?: return@update
                val guildChannels = guildMap.getOrPut(guild) { mutableListOf() }
                guildChannels.removeIf { it.id == channel.id }
                guildChannels.add(channel)
            }
            remove { pair ->
                val guildCache = guildMap[pair.first] ?: return@remove
                guildCache.removeIf { it.id == pair.second }
            }
        }
    }

}