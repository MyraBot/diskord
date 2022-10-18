package bot.myra.diskord.gateway.events.impl.guild.roles

import bot.myra.diskord.common.entities.guild.Role
import bot.myra.diskord.gateway.events.types.Event
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * [Documentation](https://discord.com/developers/docs/topics/gateway#guild-role-update-guild-role-update-event-fields)
 */
@Serializable
class RoleUpdateEvent(
    @SerialName("guild_id") val guildId: String,
    val role: Role
) : Event()