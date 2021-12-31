package diskord.common.entities.channel

import diskord.common.caching.GuildCache
import diskord.common.entities.Channel
import diskord.common.entities.guild.Guild
import diskord.rest.behaviors.channel.impl.TextChannelBehavior

data class TextChannel(
        private val data: Channel
) : TextChannelBehavior {
    override val id: String = data.id
    val guild: Guild? get() = data.guildId?.let { GuildCache[it] }
}