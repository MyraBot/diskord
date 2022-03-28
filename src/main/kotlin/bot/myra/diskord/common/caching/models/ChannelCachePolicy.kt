package bot.myra.diskord.common.caching.models

import bot.myra.diskord.common.caching.GenericCachePolicy
import bot.myra.diskord.common.entities.channel.ChannelData

class ChannelCachePolicy(
    var viewByGuild: ((String) -> List<ChannelData>)? = null,
    var associatedByGuild: ((String) -> String)? = null
) : GenericCachePolicy<String, ChannelData>() {

    fun viewByGuild(action: (String) -> List<ChannelData>) {
        viewByGuild = action
    }

    fun associatedByGuild(action: (String) -> String) {
        associatedByGuild = action
    }

    fun viewByGuild(id: String): List<ChannelData> = viewByGuild?.invoke(id) ?: view().filter { it.guildId.value == id }
    fun associatedByGuild(id: String): String? = associatedByGuild?.invoke(id) ?: view().firstOrNull { it.id == id }?.guildId?.value

}