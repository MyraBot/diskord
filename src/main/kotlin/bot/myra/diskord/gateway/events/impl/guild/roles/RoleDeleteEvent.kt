package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.gateway.events.Event
import kotlinx.serialization.SerialName

/**
 * [Documentation]()
 *
 */
class RoleDeleteEvent(
        @SerialName("guild_id") private val guildId: String,
        @SerialName("role_id") private val roleId: String
) : Event()