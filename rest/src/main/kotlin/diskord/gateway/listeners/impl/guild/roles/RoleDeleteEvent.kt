package diskord.gateway.listeners.impl.guild.roles

import diskord.gateway.listeners.Event
import kotlinx.serialization.SerialName

/**
 * [Documentation]()
 *
 */
class RoleDeleteEvent(
        @SerialName("guild_id") private val guildId: String,
        @SerialName("role_id") private val roleId: String
) : Event()