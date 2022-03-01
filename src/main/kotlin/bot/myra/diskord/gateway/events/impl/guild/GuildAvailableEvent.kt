package bot.myra.diskord.gateway.events.impl.guild

import bot.myra.diskord.common.entities.guild.Guild
import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.Serializable

@Serializable
data class GuildAvailableEvent(
    val guild: Guild
) : Event()