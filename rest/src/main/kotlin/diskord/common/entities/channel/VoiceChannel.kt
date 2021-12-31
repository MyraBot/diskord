package diskord.common.entities.channel

import diskord.common.caching.GuildCache
import diskord.common.entities.Channel
import diskord.common.entities.guild.Guild

data class VoiceChannel(
        private val data: Channel
) {
    val id: String = data.id
    val guild: Guild? get() = data.guildId?.let { GuildCache[it] }
}